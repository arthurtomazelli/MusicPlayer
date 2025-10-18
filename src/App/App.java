package App;

import java.io.File;
import java.io.IOException;
import java.nio.channels.Pipe.SourceChannel;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class App {
    private Scanner in;
    private ArrayList<File> songs;

    public App() {
        songs = new ArrayList<>();
        in = new Scanner(System.in);
        readFolder(System.getProperty("user.dir") + "\\songs");
    }

    public void exec() {
        if (songs.isEmpty()) {
            System.out.println("[Error: No songs found.]");
        } else {
            while (true) {
                selectSong();
            }
        }
    }

    private void selectSong() {
        try {
            showAllSongs();

            System.out.print("\n-> Enter the number of the song you want to play: ");
            int option = in.nextInt();
            in.nextLine();

            playSong(option);
        } catch (InputMismatchException e) {
            System.out.println("\n[Error: Input must be a number]\n");
            in.nextLine();
        } catch (Exception e) {
            System.out.println("\n[Error: " + e.getMessage() + "]\n");
            in.nextLine();
        }
    }

    private void controlMenu() {
        System.out.println("\nP = PLAY");
        System.out.println("S = STOP (pause)");
        System.out.println("R = RESET");
        System.out.println("Q = QUIT");
        System.out.println("\n<- B = BACK | N = NEXT ->");

        System.out.println();
    }

    private void playSong(int songIndex) {
        File currentFile = songs.get(songIndex - 1);

        try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(currentFile)) {
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);

            String response = "";

            System.out.println("\n----- MUSIC PLAYER -----");

            System.out.println("\nNow playing: " + getFormattedName(currentFile.getName()));
            while (!response.equalsIgnoreCase("Q")) {
                controlMenu();

                System.out.print("-> Enter your option: ");
                response = in.nextLine();

                char responseChar = response.toUpperCase().charAt(0);

                switch (responseChar) {
                    case 'P':
                        clip.start();
                        break;
                    case 'S':
                        clip.stop();
                        break;
                    case 'R':
                        clip.setMicrosecondPosition(0);
                        break;
                    case 'Q':
                        clip.close();
                        return; 
                    case 'N':
                        clip.close();
                        if (lastSong(songIndex)) {
                            playSong(1);
                        } else {
                            playSong(songIndex + 1);
                        }
                        return; 
                    case 'B':
                        clip.close();
                        if (firstSong(songIndex)) {
                            playSong(songs.size());
                        } else {
                            playSong(songIndex - 1);
                        }
                        return; 
                    default:
                        System.out.println("\nInvalid option.");
                }

            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println("\n[Error: Invalid option. Re-select the song and try again]\n");
        } catch (LineUnavailableException e) {
            System.out.println("Unable to access audio resource");
        } catch (UnsupportedAudioFileException e) {
            System.out.println("Audio file is not supported.");
        } catch (IOException e) {
            System.out.println("Something went wrong.");
        }

    }

    private boolean firstSong(int songIndex) {
        if (songIndex - 1 == 0) {
            return true;
        }

        return false;
    }

    private boolean lastSong(int songIndex) {
        if (songIndex == songs.size()) {
            return true;
        }

        return false;
    }

    private String getFormattedName(String name) {
        name = name.substring(0, name.length() - 4);

        return name;
    }

    private void readFolder(String pathName) {
        File folder = new File(pathName);

        File[] Allfiles = folder.listFiles();

        if (Allfiles != null) {
            for (File file : Allfiles) {
                if (file.getName().endsWith(".wav")) {
                    songs.add(file);
                }
            }
        }
    }

    private void showAllSongs() {
        System.out.println("===== SONGS AVAILABLE =====");
        System.out.println("\n[Number | Name]");

        for (int i = 0; i < songs.size(); i++) {
            String name = getFormattedName(songs.get(i).getName());

            System.out.printf("\n[%d] - %s", i + 1, name);
        }

        System.out.print("\n");
    }
}
