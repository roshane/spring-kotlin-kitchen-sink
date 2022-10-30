package com.example.skks.orca.http

import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse

class HomeHandler {

    fun index(req: ServerRequest) =
        ServerResponse
            .ok()
            .body("hello there")
}