package com.example.skks

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KitchenSinkApplication

fun main(args: Array<String>) {
	runApplication<KitchenSinkApplication>(*args)
}
