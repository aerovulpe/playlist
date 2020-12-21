package me.aerovulpe

import java.io.RandomAccessFile
import java.nio.file.Files
import java.nio.file.Paths

private val config =
  """
    #EXTM3U
  """.trimIndent()

fun main(vararg args: String) {
  var rootsStartIndex = 0

  val name = if (args.isNotEmpty() && args[0] == "--name") {
    rootsStartIndex = 2

    args[1]
  } else "playlist"

  val roots =
    if (rootsStartIndex >= args.size)
      listOf(System.getProperty("user.dir"))
    else
      args.slice(rootsStartIndex until args.size)

  val separator = System.getProperty("line.separator")

  RandomAccessFile("./$name.m3u", "rw")
    .use { file ->
      file.writeBytes(config + separator)

      roots.forEach { root ->
        Files.walk(Paths.get(root))
          .filter {
            Files.probeContentType(it)
              ?.startsWith("video")
              ?: false
          }.forEach {
            file.writeBytes("${it.toAbsolutePath()}$separator")
          }
      }
    }
}
