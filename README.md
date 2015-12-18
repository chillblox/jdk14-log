# jdk-log
Simple to use `java.util.logging.Logger` wrapper.

# Features

* uses String.format() instead of the default message formatter
* provides a callback so that you can use StringBuilder, '+' operator, ...
* provides parameterization in the presence of a throwable

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
	}
}
```

Running Test.java gives:

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
