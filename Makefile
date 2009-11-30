
JAVAC			= javac -g #-Xlint
CLASSPATH = .:jung/jung-visualization-2.0.jar:jung/jung-graph-impl-2.0.jar:jung/jung-algorithms-2.0.jar

all: jsysnet

install: jsysnet

JSysNet.class: 
	$(JAVAC) -classpath $(CLASSPATH) JSysNet.java

jsysnet: JSysNet.class

jar: jsysnet
	jar -classpath $(CLASSPATH) -cmf manifest.txt JSysNet.jar `find . -name \*.class` `find gnu -name \*.properties`;

test:
	$(JAVAC) Test.java

clean:
	find . -name \*.class -exec rm {} \;
	find . -name JSysNet.jar -exec rm {} \;

