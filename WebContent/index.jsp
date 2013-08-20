<%@ page import="uk.ac.manchester.cs.n8eo.N8EO"%>
<%@ page import="org.semanticweb.owlapi.util.VersionInfo"%>
<%@ page import="org.semanticweb.owlapi.model.IRI"%>
<%@ page import="org.semanticweb.owlapi.apibinding.OWLManager"%>
<%@ page import="org.semanticweb.owlapi.model.OWLOntologyManager"%>
<%@ page import="org.semanticweb.owlapi.model.OWLOntology"%>
<%@ page import="org.semanticweb.owlapi.model.OWLOntologyCreationException"%>

<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="header.html"%>

<%
	OWLOntologyManager man = OWLManager.createOWLOntologyManager();
	OWLOntology ont = null;
	try {
		String webOntFile = "https://github.com/rsgoncalves/n8eo-browser/blob/master/ontology/N8EO.owl";
		ont = man.loadOntologyFromOntologyDocument(IRI.create(webOntFile));
	} catch (OWLOntologyCreationException e) {
		e.printStackTrace();
	}
	N8EO n = new N8EO(ont);
%>

<!-- Begin Content -->
<a href="http://www.manchester.ac.uk/" class="regular" target="_blank"><img alt="" src="images/manlogo.png" width="106" height="45"></a>
<img alt="" src="" width="65">
<img alt="" src="images/logo.png" width="750" height="45"/>
<img alt="" src="" width="65">
<a href="http://www.epsrc.ac.uk/" class="regular" target="_blank"><img alt="" src="images/epsrclogo.png" width="90" height="35"></a>
<br/><br/>

<div id="content">
	<br />
	<div class="search" id="eqSearch">Equipment</div>
	<div class="search" id="eqDetails">Term Usage</div>
	<div class="search"></div>
	<br />
	<div class="dimension" id="equipment">
		<%
			String equipTree = n
					.printHierarchy("Equipment");
			out.print(equipTree);
		%>
	</div>
	<div class="details" id="details">
		&nbsp;&nbsp;To do:
		<ul>
			<li style="list-style-type:square">Search functionality on each facet</li>
		</ul>
	</div>
	<br />
	<div class="buttons">&nbsp;
		<a href="javascript:;" class="regular" onClick="clearSelection()">Clear Selection</a>&nbsp;|&nbsp;
		<a href="javascript:;" class="regular" onClick="collapseAll('equipment')">Collapse All</a>&nbsp;|&nbsp;
		<a href="javascript:;" class="regular" onClick="expandAll('equipment')">Expand All</a>
	</div>
	<div class="buttons"></div>
	<div class="buttons"></div>
	<br /> <br />
	<div class="search" id="fSearch">Function</div>
	<div class="search" id="tSearch">Technique</div>
	<div class="search" id="lSearch">Instances</div>
	<br />
	<div class="dimension" id="function">
		<%
			String functionTree = n
					.printHierarchy("Function");
			out.print(functionTree);
		%>
	</div>
	<div class="dimension" id="technique">
		<%
			String techTree = n
					.printHierarchy("Technique");
			out.print(techTree);
		%>
	</div>
	<div class="dimension" id="instances">
		<%
			String instanceTree = n.printInstancesHierarchy();
			out.print(instanceTree);
		%>
	</div>
	<br />
	<div class="buttons">&nbsp;
		<a href="javascript:;" class="regular" onClick="clearSelection()">Clear Selection</a>&nbsp;|&nbsp;
		<a href="javascript:;" class="regular" onClick="collapseAll('function')">Collapse All</a>&nbsp;|&nbsp;
		<a href="javascript:;" class="regular" onClick="expandAll('function')">Expand All</a>
	</div>
	<div class="buttons">&nbsp;
		<a href="javascript:;" class="regular" onClick="clearSelection()">Clear Selection</a>&nbsp;|&nbsp;
		<a href="javascript:;" class="regular" onClick="collapseAll('technique')">Collapse All</a>&nbsp;|&nbsp;
		<a href="javascript:;" class="regular" onClick="expandAll('technique')">Expand All</a>
	</div>
	<div class="buttons">&nbsp;
		<a href="javascript:;" class="regular" onClick="clearSelection()">Clear Selection</a>&nbsp; <!--|&nbsp;
		<a href="javascript:;" class="regular" onClick="collapseAll('instances')">Collapse All</a>&nbsp;|&nbsp;
		<a href="javascript:;" class="regular" onClick="expandAll('instances')">Expand All</a> -->
	</div>
	<br/><br/>
</div>
<!-- End Content -->

<%@ include file="footer.html"%>