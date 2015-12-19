**jdk-log** is a simple to use framework that wraps `java.util.logging.Logger`.

Beside parameterized logging and logging level testing API, it also provides logging callbacks and parameterization of logging statements in presence of an exception.

# Features

* uses String.format() instead of the default message formatter
* provides logging callbacks so that you can use StringBuilder, '+' operator, ...
* parameterization of logging statements in presence of an exception

# Example code

```
package test.logging;

import com.veracloud.logging.Log;
import com.veracloud.logging.Log.Callback;
import com.veracloud.logging.LogFactory;

public class Test {
    private static Log LOG = LogFactory.get(Test.class);

    public static void main(final String[] args) {

        LOG.i("args.length: %d", args.length);

        LOG.w(new RuntimeException("Error Message"), "args.length: %d", args.length);

        LOG.e(new RuntimeException("Error Message"), new Callback() {
            @Override
            public String getMessage() {
                return "args.length: " + args.length;
            }
        });

        // Or, using the logging level testing API...

        if (LOG.isDebugEnabled()) {
            LOG.d("args.length: " + args.length);
        }
    }
}
```

Running Test.java gives (DEBUG was disabled):

```
Dec 19, 2015 3:35:23 AM test.logging.Test main
INFO: args.length: 0
Dec 19, 2015 3:35:23 AM test.logging.Test main
WARNING: args.length: 0
java.lang.RuntimeException: Error Message
	at test.logging.Test.main(Test.java:14)

Dec 19, 2015 3:35:23 AM test.logging.Test main
SEVERE: args.length: 0
java.lang.RuntimeException: Error Message
	at test.logging.Test.main(Test.java:16)
```

# Performance

Running 10 times the benchmark listed at the end of this section, I get:

```
$ java -cp . test.logging.Test 2> /dev/null
d1=16.984000s, d2=16.534000s, d3=17.542000s, d4=15.653000s, d5=15.770000s, d6=22.128000s
$ java -cp . test.logging.Test 2> /dev/null
d1=17.859000s, d2=16.417000s, d3=16.925000s, d4=15.503000s, d5=15.546000s, d6=22.198000s
$ java -cp . test.logging.Test 2> /dev/null
d1=16.991000s, d2=16.162000s, d3=17.334000s, d4=15.728000s, d5=15.974000s, d6=23.260000s
$ java -cp . test.logging.Test 2> /dev/null
d1=18.097000s, d2=17.083000s, d3=17.535000s, d4=16.471000s, d5=16.666000s, d6=23.109000s
$ java -cp . test.logging.Test 2> /dev/null
d1=17.200000s, d2=16.030000s, d3=16.913000s, d4=15.708000s, d5=15.681000s, d6=22.032000s
$ java -cp . test.logging.Test 2> /dev/null
d1=17.436000s, d2=16.927000s, d3=17.180000s, d4=16.089000s, d5=16.615000s, d6=22.583000s
$ java -cp . test.logging.Test 2> /dev/null
d1=16.741000s, d2=16.948000s, d3=16.905000s, d4=15.790000s, d5=15.805000s, d6=22.187000s
$ java -cp . test.logging.Test 2> /dev/null
d1=17.463000s, d2=16.646000s, d3=17.734000s, d4=16.030000s, d5=16.288000s, d6=22.722000s
$ java -cp . test.logging.Test 2> /dev/null
d1=17.407000s, d2=16.525000s, d3=17.231000s, d4=15.971000s, d5=16.236000s, d6=22.817000s
$ java -cp . test.logging.Test 2> /dev/null
d1=17.594000s, d2=16.735000s, d3=17.218000s, d4=15.992000s, d5=16.547000s, d6=22.860000s
```

where:

* `d1` uses the `+` operator
* `d2` uses `StringBuilder`
* `d3` uses `String.format()`
* `d4` uses logging callback with `+` operator
* `d5` uses logging callback with `StringBuilder`
* `d6` uses default `java.util.logging.Logger` parameterized logging
 
The average values for `d1`, `d2`, `d3`, `d4`, `d5` and `d6` are:

```
<d1>=17.3772s, <d2>=16.6007s, <d3>=17.2517s, <d4>=15.8935s, <d5>=16.1128s, <d6>=22.5896s
```
				
and, the winner is: logging callbacks with `+` operator!!! 

Compared to parameterized logging, logging callbacks are ~2 seconds faster than String.format() and ~6 seconds faster than the default parameterized logging of `java.util.loggin.Logger`. Ok, this is an insignificant difference for the 1 million logs generated.

With 5 parameters I get similar results:

```
<d1>=17.7771s, <d2>=17.2128s, <d3>=19.2756s, <d4>=16.3927s, <d5>=16.5691s, <d5>=22.6473s
```

Anyway, more important is logging performance for *disabled* logging statements, which are significantly faster by using features like parameterized logging, logging level testing, or logging callbacks. Running the same benchmark with *disabled* logging statements, I get:

```
<d1>=0.0083s, <d2>=0.0055s, <d3>=0.0066s, <d4>=0.0059s, <d5>=0.0061, <d6>=0.0052s
```

where you can see that the overhead of using jdk-log instead of `java.util.logging.Logger`, which is insignificant even for the 1 million log statements used in this benchmark.

### Benchmark code:

```
package test.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.veracloud.logging.Log;
import com.veracloud.logging.Log.Callback;
import com.veracloud.logging.LogFactory;

public class Test {
	private static Log LOG = LogFactory.get(Test.class);
	private static Logger _LOG = Logger.getLogger(Test.class.getName());

	private static int MAX = 1000*1000;

	public static void main(String[] args) {
		long start;
		double d1, d2, d3, d4, d5, d6;
		
		// ---
		start = System.currentTimeMillis();
		for (int i = 0; i < MAX; i++) {
			log_1(args);
		}
		d1 = (double) (System.currentTimeMillis() - start) / 1000D;
		
		// ---
		
		start = System.currentTimeMillis();
		for (int i = 0; i < MAX; i++) {
			log_2(args);
		}
		d2 = (double) (System.currentTimeMillis() - start) / 1000D;
		
		// ---
		
		start = System.currentTimeMillis();
		for (int i = 0; i < MAX; i++) {
			log_3(args);
		}
		d3 = (double) (System.currentTimeMillis() - start) / 1000D;
		
		// ---
		
		start = System.currentTimeMillis();
		for (int i = 0; i < MAX; i++) {
			log_4(args);
		}
		d4 = (double) (System.currentTimeMillis() - start) / 1000D;
		
		// ---
		
		start = System.currentTimeMillis();
		for (int i = 0; i < MAX; i++) {
			log_5(args);
		}
		d5 = (double) (System.currentTimeMillis() - start) / 1000D;
		
		// ---
		
		start = System.currentTimeMillis();
		for (int i = 0; i < MAX; i++) {
			log_6(args);
		}
		d6 = (double) (System.currentTimeMillis() - start) / 1000D;
		
		System.out.println(String.format("d1=%fs, d2=%fs, d3=%fs, d4=%fs, d5=%fs, d5=%fs", 
				d1, d2, d3, d4, d5, d6));
	}

	private static void log_1(String[] args) {
		if (LOG.isErrorEnabled()) {
			LOG.e("args.length: " + args.length);
		}
	}
	
	private static void log_2(String[] args) {
		if (LOG.isErrorEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("args.length: ").append(args.length);
			LOG.e(sb.toString());
		}
	}
	
	private static void log_3(String[] args) {
		LOG.e("args.length: %d", args.length);
	}
	
	private static void log_4(final String[] args) {
		LOG.e(new Callback() {
			@Override
			public String getMessage() {
				return "args.length: " + args.length;
			}
		});
	}
	
	private static void log_5(final String[] args) {
		LOG.e(new Callback() {
			@Override
			public String getMessage() {
				StringBuilder sb = new StringBuilder();
				sb.append("args.length: ").append(args.length);
				return sb.toString();
			}
		});
	}
	
	private static void log_6(String[] args) {
		_LOG.log(Level.SEVERE, "args.length: {0}", args.length);
	}
}
```
