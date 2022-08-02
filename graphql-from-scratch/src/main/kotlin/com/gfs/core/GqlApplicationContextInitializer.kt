package com.gfs.core

import com.gfs.core.AppContextConfig.beans
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext

class GqlApplicationContextInitializer : ApplicationContextInitializer<GenericApplicationContext> {
    override fun initialize(applicationContext: GenericApplicationContext) {
        beans.initialize(applicationContext)
    }
}