
BioNet
=======

BioNet is a visualization tool for experimental data. It allows interactive data mining of experimental data. 
BioNet currently has 3 views:

* Correlation
* Comparartive Analysis
* Time Course Study

BioNet is still alpha level software, and so some things will not work properly at this time. The Correlation
veiw is mostly complete, and initial work on Comparative Analysis has been done, but it is currently not very 
functional. Time course study is not yet implemented.


Getting and Running BioNet
---------------------------

As BioNet is currently alpha level software, there are no releases as of yet. To obtain a copy of BioNet, you must
obtain and compile a copy of the code from github. You may check out the code with svn or git. To check out the code 
with git from the command line, navigate to the directory you wish to keep the code in, and type:

`git clone git://github.com/mcgrew/bionet.git`

or

`git clone http://github.com/mcgrew/bionet.git`

__Or__, if you prefer subversion:

`svn checkout http://svn.github.com/mcgrew/bionet.git`

You may also obtain a compressed archive of the code by visiting <http://github.com/mcgrew/bionet> and clicking on the
_Downloads_ button. In order to compile and run BioNet you will need the 
[Java JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html) from Oracle, along with 
[Apache Ant](http://ant.apache.org/). The OpenJDK is not currently recommended, as the 2D graphics performance is
significantly slower than the official Oracle release. 

To compile BioNet, first change to the root directory of the repository. To compile a distributable jar file which 
contains all necessary libraries, simply run the command

`ant dist`

After this, you should have a file called `BioNet.jar` in your current directory. You may then run BioNet by typing:

`java -jar BioNet.jar`

There are a few other commands which may be helpful, such as `ant jar` to compile a version without the included 
libraries.  In this case you will need the _lib_ folder from the repository in order to run BioNet. `ant run` will 
simply run BioNet without creating a _jar_ file.

### Development

If you wish to contribute to BioNet or just make some changes to the code, you will find the API documentation in
docs/api/index.html from the root of the repository. This is normally kept up to date, but you may wish to run 
`ant docs` to regenerate the documentation to be sure. As BioNet is still under heavy development, we are not
soliciting any bug reports at this time, but you are welcome to submit patches via a github pull request, or by
contacting [the developer](http://github.com/mcgrew) on github.


Getting Started
---------------

Once you have BioNet running, you are presented with an empty window. By going to the _File_ menu and selecting
_Open_, you will be presented with a file dialog which will allow you to open a BioNet project folder. There is
a set of example data in the _sampleData/text_ directory in the repository. This should get you started. Select this
folder and click _Open_, after which you will be presented another dialog to choose which view you would like to
see. Select the proper view on the right, and Choose the experiment(s) you would like to view. _Correlation View_
only allows you to choose one experiment, while _Comparative Analysis_ and _Time Course Study_ will allow multiple
experiments to be selected.


Correlation View
----------------

The correlation view for BioNet allows you to see Correlations between molecules measured in an experiment. Correlation
View will create a network view of the data and allow you to filter connections by correlation value. The network graph
will consist of a number of nodes, each of which will denote a molecule in the experimental data, each with multiple
sample values. For any 2 molecules, the sample values are used to determin their correlation value, denoted by the
edges on the network graph. Three methods are available for calculating the correlation: Pearson, Spearman Rank, and 
Kendall Tau b Rank. Each of these is selectable in the _calculation_ menu.

The network may be modified in several ways.  On the left side of this view are 2 filters: Molecule List and 
Correlation Filter. The __Molecule List__ is a list of molecules (nodes) in the network graph. Each of the entries
in this list are accompanied by a check box. Clicking on this check box will _uncheck_ the box, removing its node from
the graph. In addition, there is a _Search_ box at the top of the Molecule List. Entering text in this box will filter
the molecule list based on what you enter, hiding anything which doesn't match. Wild card characters may also be entered
into this box, `?` for a single character or `*` for multiple characters. In addition, some regular expression syntax will be
accepted. Clicking the _clear_ button below the box clears the search box and returns the list to contain all nodes.

The __Correlation Filter__ allows you to specify what correlations (edges) are visible on the graph. All 3 correlation
calculation methods limit the values to between -1 and 1. The filter may be adjusted to contain any range between
0 and 1. Negative values whose absolute value fall within the range will also be displayed. The colors of the edges
may be used to infer their values, which are indicated on the legend in the lower left corner of the graph. This also
controls what correlations will be colored in on the heat map layout. In that mode, anything outside the correlation
range specified will be colored white and details will not be viewable by clicking.

The network may also be controlled via the _View_ menu. This menu contains options for zooming in and out on the graph,
 changing the color mode.. It also allows you to manipulate the selection and visible nodes.

You may also manipulate the graph directly with the mouse. You may click and drag nodes around the graph. You may also
zoom in/out with the mouse wheel. By right-clicking on a node, you may also select all nodes correlated to it directly 
or all nodes in its subnetwork. Selecting _Details_ also allows you to view more details about the molecule indicated 
by this node.



### The Information Panel

Just below the graph is the graph information panel. This panel contains information about the graph and any selected 
nodes or edges. 

* __Molecules Tab__: This tab contains a table which displays brief details about all of the molecules (nodes) in the 
	graph which are part of
the selection. 
* __Correlations Tab__: This tab contains details about the selected Correlations (edges) in the graph. It 
	contains the id of the node at each end of the edge, along with the Pearson, Spearman Rank, and Kendall Tau-b rank 
	values of the correlations. The currently displayed correlation value is highlighted in yellow.
* __Diplay Conditions Tab__: This tab merely shows the currently selected layout along with the current selected 
	correlation calculation method and selected groups.
* __Topological Information Tab__: This tab contains information about the network topology:
	* the number of nodes 
	* the number of edges 
	* the number of nodes which have at least one edge
	* the average number of neighbors for all nodes
	* The network diameter, i.e. the maximum shortest path between any 2 nodes.
	* The characteristic path length, i.e. the average shortest path between any 2 nodes.
* __Node Degree Distribution Tab__: This tab contains a bar graph showing the number neighbors for each node in 
	the graph. On the X axis is the neighbor count (the number of neighbors a node has), and on the Y axis are the number 
	of nodes which have this many neighbors.
* __Correlation Distribution Tab__: This shows the distribution of correlations between any 2 nodes on the graph, 
	whether the correlations are visible or not.
* __Neighbornood Connectivity Tab__: This tab contains a bar graph about neighborhood connectivity.  Along the X 
	axis are the number of neighbors a node has. Along the Y axis are the average degree of each of those neighbors.



### Layouts

There are several automatic layouts available in correlation view. When the view is loaded, the network will be in
multiple circle layout.

* __Multiple Circle Layout__: Multiple Circle Layout organizes the nodes into 3 groups, one for up-regulated molecules,
	one for down-regulated molecules, and one for neutral molecules. Each group will be a circle on the network graph. The
	grouping is based on the sample groups you have chosen and the fold change parameter you have set.
* __Single Circle Layout__: Single Circle Layout is just that, all nodes are laid out in one large circle.
* __Random__: Nodes are placed randomly on the graph.
* __Kamada-Kawai__: Nodes are laid out according to the Kamada-Kawai Algorithm implementation in 
	[JUNG](http://jung.sourceforge.net). This is an iterative layout, and therefore the network may still be in a state 
	of flux when it is first loaded. This may cause some nodes to move around after the initial layout.
* __Spring Layout__: This layout starts with all nodes in the `Single Circle` layout. It
	uses a slightly modified version of the Fruchterman-Reingold algorithm to refine the placement of the nodes in the 
	network. The modification to the algorithm is that the optimal node placement is weighted by the correlation value. 
	This layout method is iterative and animated.
* __Heat Map__: The heat map view is different from the other layouts in that is not a network graph, but instead shows 
	all of the possible correlations in a heat map, with colors to denote their value. You may click any of these colored 
	blocks to view more detail about the correlation.

### The View Menu

* __Color__: This allows you to switch between high contrast color and normal color modes.
* __Zoom Control__: The first section of this menu is the zoom funtionality. These 3 entries allow you to adjust the 
	zoom level of the graph.
* __Selection Control__: This second section allows you to manipulate the graph selection.
	* __Select All__: Selects all nodes and edges in the graph.
	* __Clear Selection__: De-selects all nodes and edges in the graph.
	* __Invert Selection__: Selects all nodes and edges which are not selected and de-selects those that are.
	* __Select Correlated to Selection__: Examines the current selected nodes and selects all nodes which are connected to
		the selected nodes by an edge, also selecting that edge.
* __Visibility Control__: This third section allows you to control which molecules are visible on the graph. The 
	changes are reflected in the _Molecule List_. 
	* __Hide Selected__: Hides all nodes which are currently selected.
	* __Hide Unselected__: Hides all nodes which are not currently selected.
	* __Hide Uncorrelated to Selection__: Hides all nodes which are not connected to a selected node by an edge.
	* __Hide Orphans__: Hides all nodes which have a node degree of 0, i.e. they are not connected to any other node on the
		graph.
	* __Show All Correlated to Visible__: Brings back nodes which are currently hidden, but would be connected to a currently
		visible node on the graph if they were not hidden.
	* __Save Main Graph Image__: This will display a save dialog allowing you to save the network graph (or heat map) currently
		displayed to an image file. The file will have the same dimensions as the currently displayed graph.


Development Team
----------------

### Lead Developer

![Thomas McGrew's Avatar](http://www.gravatar.com/avatar/3ca5607c970cf65c310eb48937b5092f?s=60)
[Thomas McGrew](http://github.com/mcgrew)

