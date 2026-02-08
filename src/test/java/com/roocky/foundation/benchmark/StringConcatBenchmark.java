package com.roocky.foundation.benchmark;

import com.roocky.foundation.api.model.ClaimPermission;

public class StringConcatBenchmark {

    public static void main(String[] args) {
        long iterations = 10_000_000;

        // Simulating inputs
        ClaimPermission permission = ClaimPermission.BLOCK_BREAK;
        String playerName = "TestPlayer";
        String pos = "[x=10, z=20]"; // Simulating ChunkPos.toString()

        // Warmup
        System.out.println("Warming up...");
        for (int i = 0; i < 100_000; i++) {
            runIteration(permission, playerName, pos, false);
            runIterationOptimized(permission, playerName, pos, false);
        }

        System.out.println("Running benchmark...");

        // Benchmark Unoptimized
        long start = System.nanoTime();
        for (long i = 0; i < iterations; i++) {
            runIteration(permission, playerName, pos, false); // Debug mode OFF
        }
        long end = System.nanoTime();
        double timePerOpUnoptimized = (double)(end - start) / iterations;

        System.out.println("Unoptimized (Debug OFF): " + timePerOpUnoptimized + " ns/op");

        // Benchmark Optimized
         start = System.nanoTime();
        for (long i = 0; i < iterations; i++) {
            runIterationOptimized(permission, playerName, pos, false); // Debug mode OFF
        }
        end = System.nanoTime();
        double timePerOpOptimized = (double)(end - start) / iterations;

        System.out.println("Optimized (Debug OFF): " + timePerOpOptimized + " ns/op");

        System.out.printf("Improvement: %.2fx faster%n", timePerOpUnoptimized / timePerOpOptimized);
    }

    private static void runIteration(ClaimPermission permission, String playerName, String pos, boolean debugMode) {
        // Current code: String is built regardless of debugMode
        String debugPrefix = "Checking " + permission + " for " + playerName + " in " + pos + ": ";

        if (debugMode) {
            // log(debugPrefix + ...)
            consume(debugPrefix);
        }
    }

    private static void runIterationOptimized(ClaimPermission permission, String playerName, String pos, boolean debugMode) {
        if (debugMode) {
             String debugPrefix = "Checking " + permission + " for " + playerName + " in " + pos + ": ";
             consume(debugPrefix);
        }
    }

    private static void consume(String s) {
        // prevent dead code elimination
        if (s.hashCode() == 0) {
            System.out.print("");
        }
    }
}
