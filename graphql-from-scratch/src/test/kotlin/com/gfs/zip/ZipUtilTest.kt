package com.gfs.zip

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.crypto.KeyGenerator

class ZipUtilTest {

    companion object{
        private val logger = LoggerFactory.getLogger(ZipUtilTest::class.java)
    }

    @Test
    fun `should create zip file`(){
        val fos = FileOutputStream("compressed.zip")
        val zipOut = ZipOutputStream(fos, StandardCharsets.UTF_8)
        val resource = ZipUtilTest::class.java.classLoader.getResource("test-datafetcher.json")
        if(resource!=null){
            val file = File(resource.toURI())
            val zipEntry = ZipEntry(file.name)
            zipOut.putNextEntry(zipEntry)
            zipOut.write(file.readBytes())
            zipOut.close()
            logger.info("created file: {}", "compressed.zip")
        }else{
            logger.warn("File not found")
        }
        zipOut.close()
        fos.close()
    }

    @Test
    fun `key generator`(){
        val keyGenerator = KeyGenerator.getInstance("AES")
        val key = keyGenerator.generateKey()
        logger.info("key: {}",key)
    }


}