/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package player;

import com.sun.xml.ws.developer.StreamingDataHandler;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 *
 * @author Marek Potociar
 */
public class Main {

    private static enum Composition {
        /*
        Lady Gaga - Just Dance (Dance)
        Lily Allen - The Fear (Alternative)
        Britney Spears - Womanizer (Pop Rock)
        Metallica - ... (Rock Ballad)
        Carlos Santana & Buddy Guy at Montreux Jazz Festival (Electric Jazz)
        J.S.Bach - Air (Orchestral Suite)
        Manowar - Kings Of Metal (Heavy Metal)
         */

        DANCE(1, "Lady Gaga", "Just Dance", "Dance", "dance"),
        ALTERNATIVE(2, "Lily Allen", "The Fear", "Alternative Rock", "alternative"),
        POP_ROCK(3, "Britney Spears", "Womanizer", "Pop", "pop"),
        ROCK_BALLAD(4, "Metallica", "Turn The Page", "Rock Ballad", "ballad"),
        HEAVY_METAL(5, "Manowar", "Kings Of Metal", "Heavy Metal", "metal"),
        JAZZ(6, "Carlos Santana & Buddy Guy", "Montreux Jazz Festival", "Jazz", "jazz"),
        ORCHESTRAL(7, "J.S.Bach", "Air", "Classical", "classical");

        private static Composition getById(int chosenId) {
            for (Composition c : values()) {
                if (c.id == chosenId) {
                    return c;
                }
            }

            return null;
        }
        //
        final int id;
        final String artist;
        final String songTitle;
        final String genre;
        final String fileName;

        private Composition(int id, String artist, String songTitle, String genre, String fileName) {
            this.id = id;
            this.artist = artist;
            this.songTitle = songTitle;
            this.genre = genre;
            this.fileName = fileName;
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        StreamingDataHandler sdh = null;
        InputStream is = null;
        try {
            System.out.println("Choose genre to start audio streaming:");

            Composition chosen;
            do {
                for (Composition c : Composition.values()) {
                    System.out.println(String.format("%d. %s", c.id, c.genre));
                }

                int chosenId = new Scanner(System.in).nextInt();
                chosen = Composition.getById(chosenId);

                if (chosen == null) {
                    System.out.println("\n\nIncorrect choice. Please select a proper number in range:");
                }
            } while (chosen == null);

            System.out.println(String.format("\n\nLoading %s - %s :\n\n", chosen.artist, chosen.songTitle));

            System.out.println("Creating and configuring stream service reference... ");
            provider.AudioStreamerService service = new provider.AudioStreamerService();
            provider.AudioStreamer port = service.getAudioStreamerPort();

            System.out.println("DONE\nGeting data handler... ");
            sdh = (StreamingDataHandler) port.getWavStream(chosen.fileName);
            System.out.println("DONE\nOpening data stream... ");
            is = sdh.readOnce();
            System.out.println("DONE\nStarting audio player thread... ");
            sun.audio.AudioPlayer.player.start(is);
            System.out.println("Audio player thread started, waiting for it to finish... ");
            sun.audio.AudioPlayer.player.join();
            System.out.println("DONE");
        } finally {
            System.out.println("Closing data streams... ");
            is.close();
            sdh.close();
            System.out.println("DONE");
        }
    }
}
