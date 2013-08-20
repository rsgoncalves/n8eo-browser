/*******************************************************************************
 * This file is part of N8EO Browser.
 * 
 * N8EO Browser is licensed under a Creative Commons Attribution 3.0 Unported License.
 * 
 * Copyright 2013, The University of Manchester
 * 
 * To view a copy of the license, visit http://creativecommons.org/licenses/by/3.0/deed.en_US
 ******************************************************************************/
package uk.ac.manchester.cs.n8eo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntology;

/**
 * @author Rafael S. Goncalves <br/>
 * Information Management Group (IMG) <br/>
 * School of Computer Science <br/>
 * University of Manchester <br/>
 */
public class N8EO {
	private final String eDim = "Equipment", fDim = "Function", tDim = "Technique", lDim = "Location";
	private OWLOntology ont;
	private Set<String> facets, treePrinted;
	private Classifier classifier;
	private Map<String,Integer> trees;
	
	/**
	 * N8EO Constructor
	 * @param ont	OWL Ontology
	 */
	public N8EO(OWLOntology ont) {
		this.ont = ont;
		classifier = new Classifier(ont);
		classifier.classify();
		
		facets = new HashSet<String>(Arrays.asList(eDim,fDim,tDim,lDim));
		trees = new HashMap<String,Integer>();
		treePrinted = new HashSet<String>();
		indexAllTerms();
	}
	
	
	/**
	 * Given a concept name, get its subtree as a string
	 * @param concept	Root concept name
	 * @return Subtree of a given concept
	 */
	public String printHierarchy(String concept) {
		Map<String, Boolean> map = classifier.getDirectSubclasses(concept);
		String output = "";
		if(!map.isEmpty()) {
			if(facets.contains(concept))
				output += "<ul id='" + concept + "_Main" + trees.get(concept) + "'>\n";
			else
				output += "<ul id='" + concept + "_Children" + trees.get(concept) + "' style='display:none'>\n";
			
			for(String s : map.keySet()) {
				output += recurseOnConcept(s, map.get(s));
			}
			output += "</ul>\n";
		}
		return output;
	}

	
	/**
	 * Given a concept name and whether it has subclasses, produce the appropriate list items
	 * @param concept	Concept name
	 * @param hasChildren	Boolean value: true if concept has any children
	 * @return The appropriate list items and sub-hierarchies, where appropriate
	 */
	public String recurseOnConcept(String concept, boolean hasChildren) {
		if(treePrinted.contains(concept))
			addToMap(concept, trees);
		else
			treePrinted.add(concept);
		
		Set<String> allRelated = getRelatedTerms(concept);
		String related = stringify(allRelated, ",");
		
		Set<String> allParents = getParentsToExpand(allRelated);
		String parents = stringify(allParents, ",");
		
		Set<String> allUsage = getConceptUsage(concept);
		String usage = stringify(allUsage, ";");
		
		String output = "";
		if(hasChildren) {
			output += "<li>\n<a href='javascript:;' onClick=\"toggleSubTree('" + concept + "_Children" + trees.get(concept) + "','" + concept + "_Img" + trees.get(concept) + "')\">"
					+ "\n<img src='images/button-closed.png' id='" + concept + "_Img" + trees.get(concept) + "'/></a> \n<a href='javascript:;' id='" + concept + "' name='" + concept + 
					"' onClick=\"focusOn('" + concept + "','" + related + "'); expand('" + parents + "'); showUsg('" + usage + "')\" class='concept'>" + concept + "</a>\n</li>\n";
			output += printHierarchy(concept);
		} 
		else
			output += "<li>\n<img src='images/button-closed.png' style='visibility:hidden'/> \n<a href='javascript:;' id='" + concept + "' name='" + concept + "' " +
					"onClick=\"focusOn('" + concept + "','" + related + "'); expand('" + parents + "'); showUsg('" + usage + "')\" class='concept'>" + concept + "</a>\n</li>\n";
		return output;
	}
	
	
	/**
	 * Get usage of a concept
	 * @param term	Concept name
	 * @return Usage of concept
	 */
	private Set<String> getConceptUsage(String term) {
		return classifier.getUsageOfConcept(term);
	}
	
	
	/**
	 * Get usage of an individual
	 * @param term	Individual name
	 * @return Usage of individual
	 */
	private Set<String> getIndividualUsage(String term) {
		return classifier.getUsageOfIndividual(term);
	}
	
	
	/**
	 * Print the equipment and location instances
	 * @return HTML-formatted string 
	 */
	public String printInstancesHierarchy() {
		String output = "";
		output += "<ul id='equipmentInstances'><u>Equipment</u>";
		output += getInstances(eDim);
		output += "</ul>";
		output += "<ul id='locationInstances'><u>Location</u>";
		output += getInstances(lDim);
		output += "</ul>";
		return output;
	}
	
	
	/**
	 * Get instances of a given concept name
	 * @param concept	Concept name
	 * @return HTML-formatted string
	 */
	private String getInstances(String concept) {
		String out = "";

		for(String s : classifier.getInstancesOf(concept)) {
			Set<String> allRelated = getRelatedTermsForInstance(s);
			String related = stringify(allRelated, ",");
			
			Set<String> allParents = getParentsToExpand(allRelated);
			String parents = stringify(allParents, ",");
			
			Set<String> allUsage = getIndividualUsage(s);
			String usage = stringify(allUsage, ";");
			
			out += "<li>\n<img src='images/button-closed.png' style='visibility:hidden'/> \n<a href='javascript:;' id='" + s + "' name='" + s + "' " +
					"onClick=\"focusOn('" + s + "','" + related + "'); expand('" + parents + "'); showUsg('" + usage + "')\" class='instance'>" + s + "</a>\n</li>\n";
		}
		return out;
	}
	
	
	
	private Set<String> getRelatedTermsForInstance(String instance) {
		Set<String> related = new HashSet<String>();
		if(classifier.isInstanceOf(instance, eDim)) {
			related = classifier.getTypesOfInstance(instance);
		}
		else {
			related = classifier.getTypesOfHasLocation(instance);
			Set<String> toAdd = new HashSet<String>();
			for(String s : related) {
				toAdd.addAll(classifier.getTypesOfInstance(s));
			}
			related.addAll(toAdd);
		}
		related.remove("Nothing");
		related.remove("Thing");
		related.remove("Equipment");
		return related;
	}
	
	
	/**
	 * Add the specified concept to the given map, increasing the appearance-counter where applicable
	 * @param concept	Concept to add to map
	 * @param map	Map to be updated
	 */
	private void addToMap(String concept, Map<String,Integer> map) {
		if(map.containsKey(concept)) {
			Integer i = map.get(concept);
			i++;
			map.put(concept,i);
		}
		else
			map.put(concept, 1);
	}


	/**
	 * Given a concept name, return a JavaScript friendly String with the set of concepts 'related' to it
	 * @param concept	Concept name
	 * @return JavaScript-friendly string with the set of concepts 'related' to it
	 */
	public Set<String> getRelatedTerms(String concept) {
		Set<String> related = null;
		
		// If C => equipment
		if(classifier.isSubClassOf(concept, eDim)) {
			related = getRelatedTermsForConcept(concept);
		}
		// If C => Function: Get all equipment E s.t. E => hasFunction.C
		else if(classifier.isSubClassOf(concept, fDim)){
			related = getRelatedTermsForFiller(fDim, concept);
		}
		// If C => Technique: Get all equipment E s.t. E => usesTechnique.C
		else if(classifier.isSubClassOf(concept, tDim)){
			related = getRelatedTermsForFiller(tDim, concept);
		}
		related.addAll(classifier.getInstancesOf(concept));
		related.remove("Thing");
		related.remove("Nothing");
		return related;
	}
	
	
	/**
	 * Get the set of related terms for a specified role
	 * @param role	Role name
	 * @param concept	Concept name
	 * @return The set of terms related to the specified role
	 */
	private Set<String> getRelatedTermsForFiller(String role, String concept) {
		Set<String> related = new HashSet<String>();
		related.addAll(classifier.getSubclassesForFiller(role, concept));
		return related;
	}
	
	
	/**
	 * Get the set of related terms for a specified concept name
	 * @param concept	Concept name
	 * @return Set of related terms for a specified concept name
	 */
	private Set<String> getRelatedTermsForConcept(String concept) {
		Set<String> related = new HashSet<String>(); 
		related.addAll(classifier.getAllSubclassNames(concept));
		related.addAll(classifier.getFunctionFillers(concept));
		related.addAll(classifier.getTechniqueFillers(concept));
		return related;
	}

	
	
	/**
	 * Convert a set of strings into a single, JavaScript-interpretable string
	 * @param strings	Set of terms
	 * @return JavaScript-ready string
	 */
	private String stringify(Set<String> strings, String sep) {
		String result = "";
		int counter = 0;
		for(String s : strings) {
			counter++;
			result += s;
			if(counter < strings.size())
				result += sep;
		}
		return result;
	}
	
	
	/**
	 * Get the set of parent terms that need expanding based on a given set of terms
	 * @param terms	Set of terms
	 * @return Set of parents of the given terms that need expanding
	 */
	private Set<String> getParentsToExpand(Set<String> terms) {
		Set<String> parents = new HashSet<String>();
		for(String s : terms) {
			for(String supc : classifier.getAllSuperclassNames(s)) {
				if(!facets.contains(supc) && !supc.equals("Thing"))
					parents.add(supc + "_Children" + trees.get(supc));
			}
		}
		return parents;
	}
	
	
	/**
	 * Add all concept names to a map, that maintains multiple occurrences of the same concept name 
	 */
	public void indexAllTerms() {
		Set<String> allTerms = classifier.getAllSubclassNames(ont.getOWLOntologyManager().getOWLDataFactory().getOWLThing());
		for(String s : allTerms)
			addToMap(s, trees);
	}
}
