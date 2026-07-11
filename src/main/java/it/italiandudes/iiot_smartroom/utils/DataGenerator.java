package it.italiandudes.iiot_smartroom.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

@SuppressWarnings("unused")
public final class DataGenerator {

    // Attributes
    @NotNull private static final Random RANDOMIZER = new Random();

    // Methods
    @NotNull
    public static Random getRandomizer() {
        return RANDOMIZER;
    }

    // Random Between
    /**
     * Return a random integer in range bounds from min (included) to max (excluded).
     * @param min The minimum value included.
     * @param max The maximum value excluded.
     * */
    public static int randomBetween(final int min, final int max) {
        return RANDOMIZER.nextInt(max - min) + min;
    }
    /**
     * Return a random integer in range bounds from min (included) to max (excluded).
     * @param min The minimum value included.
     * @param max The maximum value excluded.
     * */
    public static double randomBetween(final double min, final double max) {
        return RANDOMIZER.nextDouble(max - min) + min;
    }

    // Value Change Towards
    /**
     * Increases the starting value towards a target by a random step, never exceeding the target and never decreasing.
     * @param startingValue The base value before adding the step.
     * @param minStep Minimum step size.
     * @param maxStep Maximum step size.
     * @param target The value to move towards; never exceeded.
     * @return the starting value plus a random step between {@code minStep} and {@code maxStep}, clamped so it never exceeds {@code target}. If {@code startingValue} is already {@code >= target}, returns it unchanged.
     */
    public static double increaseValueTowards(double startingValue, double minStep, double maxStep, double target) {
        if (startingValue >= target) return startingValue;
        double step = randomBetween(minStep, maxStep);
        return Math.min(startingValue + step, target);
    }
    /**
     * Decreases the starting value towards a target by a random step, never going below the target and never increasing.
     * @param startingValue The base value before subtracting the step.
     * @param minStep Minimum step size.
     * @param maxStep Maximum step size.
     * @param target The value to move towards; never undershot.
     * @return the starting value minus a random step between {@code minStep} and {@code maxStep}, clamped so it never goes below {@code target}. If {@code startingValue} is already {@code <= target}, returns it unchanged.
     */
    public static double decreaseValueTowards(double startingValue, double minStep, double maxStep, double target) {
        if (startingValue <= target) return startingValue;
        double step = randomBetween(minStep, maxStep);
        return Math.max(startingValue - step, target);
    }
    /**
     * Moves the starting value towards a target by a random step, in either direction, stopping once it reaches the target.
     * @param startingValue The base value before adding/subtracting the step.
     * @param target The value to move towards.
     * @param minStep Minimum step size.
     * @param maxStep Maximum step size.
     * @return the starting value moved one random step closer to {@code target}, without overshooting it. If already within {@code minStep} of {@code target}, returns {@code target}.
     */
    public static double driftTowards(double startingValue, double target, double minStep, double maxStep) {
        if (Math.abs(startingValue - target) < minStep) return target;
        double step = randomBetween(minStep, maxStep);
        return startingValue < target ? Math.min(startingValue + step, target) : Math.max(startingValue - step, target);
    }

    // Random Sign
    /**
     * Returns the given value with a randomly assigned sign.
     * @param value The value whose sign will be randomized.
     * @return the given value with either a positive or negative sign, chosen at random.
     */
    public static int randomSign(int value) {
        return randomBetween(0, 100) % 2 == 0 ? Math.abs(value) : -Math.abs(value);
    }
    /**
     * Returns the given value with a randomly assigned sign.
     * @param value The value whose sign will be randomized.
     * @return the given value with either a positive or negative sign, chosen at random.
     */
    public static double randomSign(double value) {
        return randomBetween(0, 100) % 2 == 0 ? Math.abs(value) : -Math.abs(value);
    }

    public static final class Jitter {
        // Standard Jitter
        /**
         * Returns the starting value with a random jitter added or subtracted.
         * @param startingValue The base value before adding the jitter.
         * @param minJitter Minimum Absolute Jitter.
         * @param maxJitter Maximum Absolute Jitter.
         * @return the starting value plus or minus a random jitter between {@code minJitter} and {@code maxJitter}.
         */
        public static int jitter(int startingValue, int minJitter, int maxJitter) {
            return startingValue + randomSign(randomBetween(Math.abs(minJitter), Math.abs(maxJitter)));
        }
        /**
         * Returns the starting value with a random jitter added or subtracted.
         * @param startingValue The base value before adding the jitter.
         * @param minJitter Minimum Absolute Jitter.
         * @param maxJitter Maximum Absolute Jitter.
         * @return the starting value plus or minus a random jitter between {@code minJitter} and {@code maxJitter}.
         */
        public static double jitter(double startingValue, double minJitter, double maxJitter) {
            return startingValue + randomSign(randomBetween(Math.abs(minJitter), Math.abs(maxJitter)));
        }

        // Jitter With Clamping
        /**
         * Returns the starting value with a random jitter added or subtracted, clamping the result between {@code minValue} and {@code maxValue}.
         * @param startingValue The base value before adding the jitter.
         * @param minJitter Minimum Absolute Jitter.
         * @param maxJitter Maximum Absolute Jitter.
         * @param minValue Minimum allowed return value.
         * @param maxValue Maximum allowed return value.
         * @return the starting value plus or minus a random jitter between {@code minJitter} and {@code maxJitter}, clamped between {@code minValue} and {@code maxValue}.
         */
        public static int jitter(int startingValue, int minJitter, int maxJitter, int minValue, int maxValue) {
            return Math.clamp(startingValue + randomSign(randomBetween(Math.abs(minJitter), Math.abs(maxJitter))), minValue, maxValue);
        }
        /**
         * Returns the starting value with a random jitter added or subtracted, clamping the result between {@code minValue} and {@code maxValue}.
         * @param startingValue The base value before adding the jitter.
         * @param minJitter Minimum Absolute Jitter.
         * @param maxJitter Maximum Absolute Jitter.
         * @param minValue Minimum allowed return value.
         * @param maxValue Maximum allowed return value.
         * @return the starting value plus or minus a random jitter between {@code minJitter} and {@code maxJitter}, clamped between {@code minValue} and {@code maxValue}.
         */
        public static double jitter(double startingValue, double minJitter, double maxJitter, double minValue, double maxValue) {
            return Math.clamp(startingValue + randomSign(randomBetween(Math.abs(minJitter), Math.abs(maxJitter))), minValue, maxValue);
        }

        // Approach Or Settle At Floor
        /**
         * Decreases the starting value towards a target while above it; once at or below the target, applies a small upward-only jitter so the value never drops below the target.
         * Like driftTowards, but applies jitter when target is reach.
         * @param startingValue The base value before adding/subtracting the step.
         * @param minStep Minimum step size.
         * @param maxStep Maximum step size.
         * @param target The floor value; never undershot.
         * @return the starting value moved towards {@code target} if above it, otherwise the starting value with a small jitter added, clamped between {@code target} and {@code target + maxStep}.
         */
        public static double approachAndSettleAtFloor(double startingValue, double minStep, double maxStep, double target) {
            if (startingValue > target) {
                double step = randomBetween(minStep, maxStep);
                return Math.max(startingValue - step, target);
            }
            return jitter(startingValue, minStep, maxStep, target, target + maxStep);
        }
    }
}
