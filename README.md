# Solidity Compiler: 

Solidity Compiler and Java Wrapper Generator (Linux Only)

Use:

```

import org.cambural21.solidity.compiler.CompilerItem;
import org.cambural21.solidity.compiler.Installer;
import org.cambural21.solidity.compiler.LoggingLevel;

File source = new File("/projects//Solidity/ERC20/ERC20.sol");
File build = new File("/projects/Solidity/BUILDS/ERC20");

Installer.getInstance().setWeb3jVersion("1.4.1");

String packageName = "your.package.name";
CompilerItem item = new CompilerItem(source, build, packageName);
item.setLoggingLevel(LoggingLevel.trace);
item.compile();

```

Note: The main solidity file must be in the root directory of the project, its libraries in internal directories within it.
