package com.example.skks.config

import com.example.skks.HttpModels.Comment
import com.example.skks.HttpModels.Post
import com.example.skks.HttpModels.User
import com.example.skks.provider.CommentProvider
import com.example.skks.provider.PostProvider
import com.example.skks.provider.UserProvider
import graphql.schema.DataFetcher
import org.dataloader.BatchLoader
import org.dataloader.DataLoaderFactory
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.execution.BatchLoaderRegistry
import org.springframework.graphql.execution.RuntimeWiringConfigurer
import reactor.core.publisher.Flux
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

@Configuration
class GraphQLConfig(
    private val postProvider: PostProvider,
    private val userProvider: UserProvider,
    private val commentProvider: CommentProvider,
    private val batchLoaderRegistry: BatchLoaderRegistry
) {

    private val userDataFetcher = DataFetcher { userProvider.getAll() }
    private val postDataFetcher = DataFetcher { postProvider.getAll() }
    private val commentDataFetcher = DataFetcher { commentProvider.getAll() }
    private val postByUserDataFetcher = DataFetcher {
        val user: User = it.getSource()
        postProvider.getAllByUser(user.id)
    }
    private val userByIdDataFetcher = DataFetcher {
        val userId: Int = it.getArgument("id")
        userProvider.getById(userId)
    }
    private val commentsByPostDataFetcher = DataFetcher {
        val post: Post = it.getSource()
        commentProvider.getAllByPostId(post.id)
    }

    //TODO reference: https://www.youtube.com/watch?v=ld2_AS4l19g
    // https://github.com/graphql-java/java-dataloader#readme
    private val commentsByPostIdListBatchLoader = object : BatchLoader<Int, Comment> {
        override fun load(postIdList: MutableList<Int>?): CompletionStage<MutableList<Comment>> {
            return if (postIdList != null) {
                return CompletableFuture.supplyAsync { commentProvider.getAllByPosts(postIdList).toMutableList() }
            } else {
                CompletableFuture.completedFuture(mutableListOf())
            }
        }
    }

    private val commentsByPostIdListDataLoader = DataLoaderFactory.newDataLoader(commentsByPostIdListBatchLoader)

    init {
        //configure batch loading
        val batchLogger = LoggerFactory.getLogger("batch-loader-debug")
        batchLoaderRegistry
            .forTypePair(Int::class.java, Comment::class.java)
            .registerBatchLoader { postIdList, _ ->
                batchLogger.info("loading comments for postIdList: {}", postIdList)
                Flux.empty()
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
}
