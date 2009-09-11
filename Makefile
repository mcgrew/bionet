
JAVAC			= javac


all: sysnet

install: sysnet

Sysnet.class: 
	$(JAVAC) Sysnet.java

sysnet: Sysnet.class

clean:
	find . -name \*.class -exec rm {} \;

