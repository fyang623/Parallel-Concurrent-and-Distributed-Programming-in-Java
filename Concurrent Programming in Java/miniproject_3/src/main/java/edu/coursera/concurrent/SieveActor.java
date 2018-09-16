package edu.coursera.concurrent;

import edu.rice.pcdp.Actor;
import static edu.rice.pcdp.PCDP.finish;
import java.util.stream.IntStream;


/**
 * An actor-based implementation of the Sieve of Eratosthenes.
 *
 * TODO Fill in the empty SieveActorActor actor class below and use it from
 * countPrimes to determine the number of primes <= limit.
 */
public final class SieveActor extends Sieve {
    /**
     * {@inheritDoc}
     *
     * TODO Use the SieveActorActor class to calculate the number of primes <=
     * limit in parallel. You might consider how you can model the Sieve of
     * Eratosthenes as a pipeline of actors, each corresponding to a single
     * prime number.
     */
    @Override
    public int countPrimes(final int limit) {
        SieveActorActor[] sieveActor = new SieveActorActor[1];
        finish(() -> {
            sieveActor[0] = new SieveActorActor(3);
            IntStream.range(4, limit + 1)
                    .filter(i -> i % 2 == 1)
                    .forEach(sieveActor[0]::send);
        });
        SieveActorActor loopActor = sieveActor[0];
        int numPrimes = 1;
        while(loopActor != null){
            loopActor = loopActor.nextActor;
            numPrimes++;
        }
        return numPrimes;
    }

    /**
     * An actor class that helps implement the Sieve of Eratosthenes in
     * parallel.
     */
    public static final class SieveActorActor extends Actor {
        private int prime;
        private SieveActorActor nextActor;

        SieveActorActor(final int prime){
            this.prime = prime;
        }

        /**
         * Process a single message sent to this actor.
         *
         * TODO complete this method.
         *
         * @param msg Received message
         */
        @Override
        public void process(final Object msg) {
            int candidate = (Integer) msg;
            if (candidate % prime != 0) {
                if (nextActor == null)
                    nextActor = new SieveActorActor(candidate);
                else
                    nextActor.send(msg);
            }
        }
    }
}
