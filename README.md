# `contract-chameleon-verifast`

This repository contains the [`VeriFast`](https://github.com/verifast/verifast) adapters,
for [contract-chameleon](https://github.com/Contract-LIB/contract-chameleon).

## Available Adapters

- Export Adapters
  - `verifast-provider`
  - `verifast-applicant`

## Project Setup

Just run `./build.sh`,
which sets up the necessary git submodules,
and publishes the required dependencies locally.

## Test Setup

Set a path to the `verifast` tool at `gradle.properties`:

```
contract_chameleon_verifast_exe=tools/verifast/bin/verifast
```
