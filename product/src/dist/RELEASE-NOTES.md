# Codekvast Agent Release Notes

## 0.9.3

1. Java compiler for 1.6 & 1.7 uses correct bootclasspath

## 0.9.2

1. Now excludes trivial methods from tracking (equals(), hashCode(), toString(), compareTo(), getters, setters)

## 0.9.1

1. Added a Gentoo start script for codekvast-server and codekvast-agent

## 0.9.0

1. Improved the installation procedures in CodekvastUserManual.
1. Now CodekvastUserManual.html is self-contained. No more external images.
1. codekvast-agent now runs on Java 6

## 0.8.18

1. Improved the server web interface.
1. codekvast-agent now uses a private H2 database for storing not yet uploaded data.

## 0.8.17

1. Added /etc/init.d scripts for agent and server.
1. Documented installation of agent and server (work in progress).