#!/bin/bash

echo "Solidity Compiler Automated Test v0.0.1"
echo ""

compiler_zip="https://github.com/Cambural21/solidity-compiler/releases/download/RELEASE/SolidityCompiler.zip"
compiler_isol="https://raw.githubusercontent.com/Cambural21/solidity-compiler/main/samples/IERC20.sol"
compiler_sol="https://raw.githubusercontent.com/Cambural21/solidity-compiler/main/samples/ERC20.sol"

curl -OL $compiler_isol 
curl -OL $compiler_sol
curl -OL $compiler_zip
unzip SolidityCompiler.zip
rm SolidityCompiler.zip

mkdir BUILDS

java -jar SolidityCompiler.jar "org.company.text" "$PWD/ERC20.sol" "$PWD/BUILDS/"
