# Github-Assistant-MCP-Server

<p><em>An MCP Server written using Java and Spring AI which exposes tools to allow common AI agents to perform various Github related tasks.</em></p>

<div align="center">

  <p>
    <a href="#introduction">Introduction</a> •
    <a href="#architecture">Architecture</a> •
    <a href="#setup">Setup</a> •
    <a href="#demo">Results</a>
  </p>
</div>

## Introduction

## Architecture

## Setup

### Setting up Github Token
Corresponding to `github.token=${GITHUB_TOKEN}` in `application.properties`, you will need to set an 
environment variable named `GITHUB_TOKEN` with a valid Github Personal Access Token (PAT) that has the 
necessary permissions to perform the desired operations on Github. This token will be used by the 
application to authenticate API requests to Github.


### For Developers

Run the below commands in local before pushing code to remote.

#### Code formatting
```bash
mvnw spotless:check
```

If there are formatting issues, run the below command to fix them.
```bash
mvnw spotless:apply
```

#### Static analysis
```bash
mvnw spotbugs:check
```

#### Style/complexity analysis
```bash
mvnw pmd:check
```

## Demo

Start the server using the below command:
```bash
mvnw spring-boot:run
```

