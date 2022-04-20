# Solidity Compiler: 

Solidity Compiler and Java Wrapper Generator

Note: The main solidity file must be in the root directory of the project, its libraries in internal directories within it.


Installing Web3j On Linux:

```
curl -sL get.web3j.io | bash
```

Installing Web3j On Windows:

```
Set-ExecutionPolicy Bypass -Scope Process -Force; iex ((New-Object System.Net.WebClient).DownloadString('https://raw.githubusercontent.com/web3j/web3j-installer/master/installer.ps1'))

```

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
