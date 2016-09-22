package com.eric.vectorassault.Timer;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public abstract class BaseTimer
   {
      public static final int DURATION_INFINITY = -1;
      private volatile boolean isRunning = false;
      private volatile boolean isPaused = false;
      private volatile boolean isCanceled = false;
      private long interval;
      private long elapsedTime;
      private long duration;
      private ScheduledExecutorService execService = Executors.newSingleThreadScheduledExecutor();
      private Future<?> future = null;

      /**
       * Default constructor which sets the interval to 1000 ms (1s) and the
       * duration to {@link BaseTimer#DURATION_INFINITY}
       */
      public BaseTimer()
         {
            this(1000, -1);
         }

      /**
       * @param interval The time gap between each tick in millis.
       * @param duration The period in millis for which the timer should run. Set it to {@code Timer#DURATION_INFINITY} if the timer has to run indefinitely.
       */
      public BaseTimer(long interval, long duration)
         {
            this.interval = interval;
            this.duration = duration;
            this.elapsedTime = 0;
         }

      /**
       * Starts the timer. If the timer was already running, this call is ignored.
       */
      public void start()
         {
            System.out.println("elapseTime: " + elapsedTime);
            System.out.println("duration:  " + duration);

            if (isRunning)
               return;
            System.out.println("STARTING!!!");

            isRunning = true;
            future = execService.scheduleWithFixedDelay(new Runnable()
            {
               @Override
               public void run()
                  {
                     if (!isPaused)
                        {
                           onTick();
                           elapsedTime += BaseTimer.this.interval;
                           if (duration > 0)
                              {
                                 System.out.println(elapsedTime + " | " + duration);
                                 if (elapsedTime >= duration)
                                    {
                                       System.out.println("WEEEEEEEEE");

                                       future.cancel(true);

                                       onFinish();
                                    }
                              }
                        }
                  }
            }, 0, this.interval, TimeUnit.MILLISECONDS);
         }

      /**
       * Paused the timer. If the timer is not running, this call is ignored.
       */
      public void pause()
         {
            System.out.println("PAUSED!");
            isPaused = true;

            if (!isRunning) return;
            future.cancel(true);
            isRunning = false;
         }


      /**
       * Resumes the timer if it was paused, else starts the timer.
       */
      public void resume()
         {
            this.start();
            isPaused = false;

         }


      /**
       * This method is called periodically with the interval set as the delay between subsequent calls.
       */
      protected abstract void onTick();


      /**
       * This method is called once the timer has run for the specified duration. If the duration was set as infinity, then this method is never called.
       */
      protected abstract void onFinish();

      /**
       * Stops the timer. If the timer is not running, then this call does nothing.
       */
      public void cancel()
         {
            isCanceled = true;
            pause();
            this.elapsedTime = 0;
            this.duration = 0;
            execService.shutdownNow();
         }


      /**
       * @return the elapsed time (in millis) since the start of the timer.
       */
      public long getElapsedTime()
         {
            return this.elapsedTime;
         }

      /**
       * @return the time remaining (in millis) for the timer to stop. If the duration was set to {@code Timer#DURATION_INFINITY}, then -1 is returned.
       */
      public long getRemainingTime()
         {
            if (this.duration < 0)
               {
                  return BaseTimer.DURATION_INFINITY;
               }
            else
               {
                  return duration - elapsedTime;
               }
         }

      public long getDuration()
         {
            if (this.duration < 0)
               {
                  return BaseTimer.DURATION_INFINITY;
               }
            else
               {
                  return duration;
               }
         }

      public void setNewTimer(long interval, long duration)
         {
            isRunning = false;
            this.duration = duration;
            this.interval = interval;
            this.elapsedTime = 0;
            start();


         }

      /**
       * @return true if the timer is currently running, and false otherwise.
       */
      public boolean isRunning()
         {
            return isRunning;
         }

      public boolean isPaused()
         {
            return isPaused;
         }

      public boolean isCanceled()
         {
            return isCanceled;
         }
   }