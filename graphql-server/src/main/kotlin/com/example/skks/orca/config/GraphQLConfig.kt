package com.example.skks.orca.config

import com.example.skks.HttpModels.Comment
import com.example.skks.HttpModels.Post
import com.example.skks.HttpModels.User
import com.example.skks.provider.CommentProvider
import com.example.skks.provider.PostProvider
import com.example.skks.provider.UserProvider
import graphql.ErrorClassification
import graphql.GraphQLError
import graphql.language.SourceLocation
import graphql.schema.DataFetcher
import org.dataloader.BatchLoaderEnvironment
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.execution.BatchLoaderRegistry
import org.springframework.graphql.execution.DataFetcherExceptionResolver
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter
import org.springframework.graphql.execution.RuntimeWiringConfigurer
import reactor.core.publisher.Mono

@Configuration
class GraphQLConfig(
    private val postProvider: PostProvider,
    private val userProvider: UserProvider,
    private val commentProvider: CommentProvider,
    private val batchLoaderRegistry: BatchLoaderRegistry
) {

    private val POST_BY_USER_ID_DATA_LOADER = "POST_BY_USER_ID_DATA_LOADER"
    private val COMMENTS_BY_POST_ID_DATA_LOADER = "COMMENTS_BY_POST_ID_DATA_LOADER"

    private val userDataFetcher = DataFetcher { userProvider.getAll() }
    private val postDataFetcher = DataFetcher { postProvider.getAll() }
    private val commentDataFetcher = DataFetcher { commentProvider.getAll() }
    private val postByUserDataFetcher = DataFetcher {
        val user: User = it.getSource()
        val dataLoader = it.getDataLoader<User, List<Post>>(POST_BY_USER_ID_DATA_LOADER)
        dataLoader.load(user)
    }
    private val userByIdDataFetcher = DataFetcher {
        val userId: Int = it.getArgument("id")
        userProvider.getById(userId)
    }
    private val commentsByPostDataFetcher = DataFetcher {
        val post: Post = it.getSource()
        val dataLoader = it.getDataLoader<Int, List<Comment>>(COMMENTS_BY_POST_ID_DATA_LOADER)
        dataLoader.load(post.id)
    }

    //TODO reference: https://www.youtube.com/watch?v=ld2_AS4l19g
    // https://github.com/graphql-java/java-dataloader#readme

    init {
        //configure batch loading
        batchLoaderRegistry
            .forTypePair(Int::class.java, List::class.java)
            .withName(COMMENTS_BY_POST_ID_DATA_LOADER)
            .registerMappedBatchLoader {
                    postIds: Set<Int>,
                    _: BatchLoaderEnvironment,
                ->
                Mono.create<Map<Int, List<*>>> { sink ->
                    val commentsByPostId = commentProvider
                        .getAllByPosts(postIds.toList())
                        .groupBy { it.postId }
                    //TODO how to get generic list type from kotlin #51
                    val resultMap: Map<Int, List<*>> = postIds
                        .associateWith { commentsByPostId[it].orEmpty() }
                        .toMutableMap()
                    sink.success(resultMap)
//                    sink.error(java.lang.RuntimeException("error fetching comments for post id(s): $postIds"))
                }//.subscribeOn(Schedulers.boundedElastic())
            }

        batchLoaderRegistry
            .forTypePair(User::class.java, List::class.java)
            .withName(POST_BY_USER_ID_DATA_LOADER)
            .registerMappedBatchLoader {
                    users: Set<User>,
                    _: BatchLoaderEnvironment,
                ->
                Mono.create { sink ->
                    val usersById = users
                        .groupBy { it.id }
                    val postsByUserId = postProvider
                        .getAllByUserIds(usersById.keys.toList())
                    val result = users
                        .associateWith { postsByUserId[it.id].orEmpty() }
                    sink.success(result)
                }
            }
    }

    @Bean
    fun runtimeWiringConfigure(): RuntimeWiringConfigurer {
        //TODO reference: https://www.youtube.com/watch?v=gvIqFDNGgwU
        return RuntimeWiringConfigurer {
            it
                .type("Query") { wiring ->
                    wiring
                        .dataFetcher("users", userDataFetcher)
                        .dataFetcher("comments", commentDataFetcher)
                        .dataFetcher("posts", postDataFetcher)
                        .dataFetcher("userById", userByIdDataFetcher)
                }
                .type("User") { wiring ->
                    wiring
                        .dataFetcher("posts", postByUserDataFetcher)
                }
                .type("Post") { wiring ->
                    wiring
                        .dataFetcher("comments", commentsByPostDataFetcher)
                }

        }
    }

    @Bean
    fun dataFetcherExceptionResolver(): DataFetcherExceptionResolver = DataFetcherExceptionResolverAdapter.from {
            throwable,
            env,
        ->
        object : GraphQLError {
            private val logger = LoggerFactory.getLogger(GraphQLError::class.java)
            override fun getMessage(): String = "hello I have no idea what happened ${throwable.message}"


            override fun getLocations(): MutableList<SourceLocation> {
                logger.info("Error env: {}", env)
                return mutableListOf()
            }

            override fun getErrorType(): ErrorClassification = object : ErrorClassification {
                override fun toSpecification(error: GraphQLError?): Any {
                    return super.toSpecification(error)
                }
            }
        }
    }


}
