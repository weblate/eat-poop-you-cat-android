package dev.develsinthedetails.eatpoopyoucat.utilities

import dev.develsinthedetails.eatpoopyoucat.data.Entry
import dev.develsinthedetails.eatpoopyoucat.data.Game
import dev.develsinthedetails.eatpoopyoucat.data.GameWithEntries
import dev.develsinthedetails.eatpoopyoucat.data.Player
import org.apache.commons.codec.binary.Base64
import java.util.*

    val testGames = arrayListOf(
    Game(UUID.fromString("00000000-0000-0000-0000-000000000001"), 0, 0),
    Game(UUID.fromString("00000000-0000-0000-0000-000000000002"), 0, 0),
    Game(UUID.fromString("00000000-0000-0000-0000-000000000003"), 0, 0)
    )
    val playerOne = Player(UUID.fromString("00000000-0000-0000-0000-100000000001"), "bob")
    val playerTwo = Player(UUID.fromString("00000000-0000-0000-0000-100000000002"), "bobbie")

    @Suppress("SpellCheckingInspection")
    const val imageString = "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"

    @Suppress("SpellCheckingInspection")
    const val testImageGzipString = "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"

    val testImage = Base64.decodeBase64(imageString.toByteArray())!!
    val testImageGzipBytes = Base64.decodeBase64(testImageGzipString.toByteArray())!!
    val testGame = testGames[0]
    val entries = arrayListOf(
        Entry(
            UUID.fromString("e0000000-0000-0000-0000-000000000001"), playerOne.id,
            1, "My cat likes to eat wet food on their birthday", null, testGames[0].id, 600
        ),
        Entry(
            UUID.fromString("e0000000-0000-0000-0000-000000000002"), playerTwo.id,
            2, null, testImage, testGames[0].id, 600
        ),
        Entry(
            UUID.fromString("e0000000-0000-0000-0000-000000000003"), playerOne.id,
            3, "some cats eat hockey pucks", null, testGames[0].id, 600
        )
    )

    val gameWithEntriesAndPlayers = GameWithEntries(testGame, entries)