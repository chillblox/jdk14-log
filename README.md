jdk-log is a simple to use framework that wraps `java.util.logging.Logger`.

Beside parameterized logging and logging level testing API, it also provides logging callbacks and parameterization of logging statements in presence of an exception/throwable.

# Features

* uses String.format() instead of the default message formatter
* provides logging callbacks so that you can use StringBuilder, '+' operator, ...
* parameterization of logging statements in presence of an exception/throwable

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
		
		LOG.w(new RuntimeException(), "args.length: %d", args.length);
		
		LOG.e(new RuntimeException(), new Callback() {
			@Override
			public String getMessage() {
				return "args.length: " + args.length;
			}
		});
		
		// or, using IF statement...
	
		if (LOG.isDebugEnabled()) {
			LOG.d("args.length: " + args.length);
		}
	}
}
```

Running Test.java gives (DEBUG was disabled):

```
Dec 19, 2015 12:38:39 AM test.logging.Test main
INFO: args.length: 0
Dec 19, 2015 12:38:39 AM test.logging.Test main
WARNING: args.length: 0
java.lang.RuntimeException
	at test.logging.Test.main(Test.java:14)

Dec 19, 2015 12:38:39 AM test.logging.Test main
SEVERE: args.length: 0
java.lang.RuntimeException
	at test.logging.Test.main(Test.java:16)
```

# Performance

Running 10 times the benchmark listed at the end of this section, I get:

```
$ java -cp . test.logging.Test 2> /dev/null
d1=17.193000s, d2=16.887000s, d3=17.144000s, d4=16.060000s, d5=16.221000s
$ java -cp . test.logging.Test 2> /dev/null
d1=18.155000s, d2=18.808000s, d3=17.737000s, d4=16.242000s, d5=16.305000s
$ java -cp . test.logging.Test 2> /dev/null
d1=17.592000s, d2=16.747000s, d3=17.202000s, d4=15.777000s, d5=16.098000s
$ java -cp . test.logging.Test 2> /dev/null
d1=17.054000s, d2=16.211000s, d3=16.832000s, d4=15.327000s, d5=15.719000s
$ java -cp . test.logging.Test 2> /dev/null
d1=17.155000s, d2=16.299000s, d3=17.279000s, d4=16.072000s, d5=15.966000s
$ java -cp . test.logging.Test 2> /dev/null
d1=17.163000s, d2=16.339000s, d3=17.210000s, d4=15.606000s, d5=15.955000s
$ java -cp . test.logging.Test 2> /dev/null
d1=17.257000s, d2=16.421000s, d3=17.268000s, d4=15.932000s, d5=16.017000s
$ java -cp . test.logging.Test 2> /dev/null
d1=16.882000s, d2=16.344000s, d3=17.537000s, d4=15.694000s, d5=15.705000s
$ java -cp . test.logging.Test 2> /dev/null
d1=17.395000s, d2=16.427000s, d3=16.872000s, d4=15.378000s, d5=15.797000s
$ java -cp . test.logging.Test 2> /dev/null
d1=16.774000s, d2=16.155000s, d3=16.831000s, d4=15.387000s, d5=15.438000s
```

where: `<d1>=17.262`, `<d2>=16.6638`, `<d3>=17.1912`, `<d4>=15.7475` and `<d5>=15.9221`.
				
The winner is: logging callbacks with `+` operator!!! 

Compared to parameterized logging, logging callbacks are 2 seconds faster for 1 milion logging statements. Ok, this is an insignificant difference that could matter if the format string has more parameters.

With 5 parameters I get:

```
$ java -cp . test.logging.Test 2> /dev/null
d1=17.120000s, d2=16.732000s, d3=19.253000s, d4=16.202000s, d5=16.479000s
```

Benchmark code:

```
package test.logging;

import com.veracloud.logging.Log;
import com.veracloud.logging.Log.Callback;
import com.veracloud.logging.LogFactory;

public class Test {
	private static Log LOG = LogFactory.get(Test.class);

	private static int MAX = 1000*1000;

	public static void main(String[] args) {
		long start;
		double d1, d2, d3, d4, d5;
		
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
		
		System.out.println(String.format("d1=%fs, d2=%fs, d3=%fs, d4=%fs, d5=%fs", d1, d2, d3, d4, d5));
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
}
```
