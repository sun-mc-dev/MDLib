package com.muhammaddaffa.mdlib.scoreboard;

import java.util.List;

public class ScoreboardAnimation {

    private final List<String> frames;
    private int index = 0;

    public ScoreboardAnimation(List<String> frames) {
        this.frames = frames;
    }

    public static ScoreboardAnimation of(String... frames) {
        return new ScoreboardAnimation(List.of(frames));
    }

    public String next() {
        if (frames.isEmpty()) return "";
        String frame = frames.get(index);
        index = (index + 1) % frames.size();
        return frame;
    }

    public String current() {
        if (frames.isEmpty()) return "";
        return frames.get(index);
    }

    public void reset() {
        index = 0;
    }

    public List<String> getFrames() {
        return frames;
    }
}