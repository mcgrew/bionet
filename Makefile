
JAVAC			= javac

all: jsysnet

install: jsysnet

JSysNet.class: 
	$(JAVAC) JSysNet.java

jsysnet: JSysNet.class

jar: jsysnet
	jar -cmf manifest.txt JSysNet.jar `find . -name \*.class` `find gnu -name \*.properties`;

test:
	$(JAVAC) Test.java

clean:
	find . -name \*.class -exec rm {} \;
	find . -name JSysNet.jar -exec rm {} \;

