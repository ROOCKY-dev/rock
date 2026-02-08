package com.roocky.foundation.benchmark;

import net.minecraft.world.PersistentState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;
import java.util.function.BiFunction;

public class AllocationBenchmark {

    static class MockState extends PersistentState {
        @Override
        public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
            return nbt;
        }
    }

    @Test
    public void benchmarkTypeAllocation() {
        Supplier<MockState> factory = MockState::new;
        BiFunction<NbtCompound, RegistryWrapper.WrapperLookup, MockState> deserializer = (nbt, registry) -> new MockState();

        // Warmup
        consume(new PersistentState.Type<>(factory, deserializer, null));

        long start = System.nanoTime();
        int hash = 0;
        for (int i = 0; i < 10_000_000; i++) {
            hash += new PersistentState.Type<>(factory, deserializer, null).hashCode();
        }
        long end = System.nanoTime();
        System.out.printf("Allocation Time (10M): %.4f ms (hash: %d)%n", (end - start) / 1_000_000.0, hash);
    }

    @Test
    public void benchmarkStaticAccess() {
        Supplier<MockState> factory = MockState::new;
        BiFunction<NbtCompound, RegistryWrapper.WrapperLookup, MockState> deserializer = (nbt, registry) -> new MockState();
        PersistentState.Type<MockState> staticType = new PersistentState.Type<>(factory, deserializer, null);

        // Warmup
        consume(staticType);

        long start = System.nanoTime();
        int hash = 0;
        for (int i = 0; i < 10_000_000; i++) {
            hash += staticType.hashCode();
        }
        long end = System.nanoTime();
        System.out.printf("Static Access Time (10M): %.4f ms (hash: %d)%n", (end - start) / 1_000_000.0, hash);
    }

    private void consume(Object o) {
        if (o == null) throw new RuntimeException();
    }
}
