package ru.siksmfp.network.harness.implementation.io.simple

import ru.siksmfp.network.harness.api.Client
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.*

class IoClient(
        private val host: String,
        private val port: Int
) : Client<String> {
    private lateinit var printWriter: PrintWriter
    private lateinit var bufferedReader: BufferedReader
    private lateinit var clientSocket: Socket

    override fun start() {
        println("Connecting io client to $host:$port")
        clientSocket = Socket(host, port)
        printWriter = PrintWriter(clientSocket.getOutputStream(), false)
        bufferedReader = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
        println("Connected to $host:$port")
    }

    @Synchronized
    override fun send(message: String) {
        println("Sending $message")
        printWriter.println(message)
        printWriter.flush()
        val response = bufferedReader.readLine()
        println("Client received a response $response")
    }

    override fun stop() {
        println("Stopping io client")
        bufferedReader.close()
        printWriter.close()
        clientSocket.close()
    }

    fun test() {
        println("Start io testing")
        printWriter.println("test")
        printWriter.flush()
        val response = bufferedReader.readLine()
        if (response == "OK") {
            println("Test passed")
        } else {
            throw IllegalStateException("Sending test is failed")
        }
    }
}

fun main() {
    val client = IoClient("localhost", 8081)
    client.start()
    client.test()

    val scanner = Scanner(System.`in`)

    while (scanner.hasNext()) {
        val line = scanner.next()
        if (line == "stop") {
            break
        }
        client.send(line)
    }

    client.stop()
}