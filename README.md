# Solidity Compiler: 

Solidity Compiler and Java Wrapper Generator


Note: The main solidity file must be in the root directory of the project, its libraries in internal directories within it.

Use:

```

File source = new File("/projects//Solidity/ERC20/ERC20.sol");
File build = new File("/projects/Solidity/BUILDS/ERC20");

Installer.setWeb3jPath(new File("/path/.web3j/web3j"));

String packageName = "your.package.name";
CompilerItem item = new CompilerItem(source, build, packageName);
item.setLoggingLevel(LoggingLevel.trace);
item.compile();

```
