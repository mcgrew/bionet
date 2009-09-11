
JAVAC			= javac


all: jsysnet

install: jsysnet

JSysNet.class: 
	$(JAVAC) JSysNet.java

jsysnet: JSysNet.class

clean:
	find . -name \*.class -exec rm {} \;

