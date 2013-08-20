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

import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import uk.ac.manchester.cs.jfact.JFactFactory;
import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxObjectRenderer;

/**
 * @author Rafael S. Goncalves <br/>
 * Information Management Group (IMG) <br/>
 * School of Computer Science <br/>
 * University of Manchester <br/>
 */
public class Classifier {
	private final String ontURI = "http://www.semanticweb.org/ontologies/2012/9/N8EO.owl#";
	private OWLOntologyManager man;
	private OWLDataFactory df;
	private OWLClass bottom;
	private OWLReasoner reasoner;
	private OWLOntology ont;
	private SimpleShortFormProvider fp;
	private HashMap<OWLClass,Set<OWLClass>> functFillers, techFillers;
	private HashMap<String,HashMap<OWLClass,Set<OWLClass>>> facetMap;
	
	/**
	 * Classifier constructor
	 * @param ont	OWL ontology
	 */
	public Classifier(OWLOntology ont) {
		this.ont = ont;
		man = ont.getOWLOntologyManager();
		df = man.getOWLDataFactory();
		bottom = df.getOWLNothing();
		reasoner = new JFactFactory().createReasoner(ont);
		fp = new SimpleShortFormProvider();
		
		functFillers = new HashMap<OWLClass,Set<OWLClass>>();
		techFillers = new HashMap<OWLClass,Set<OWLClass>>();
		facetMap = new HashMap<String,HashMap<OWLClass,Set<OWLClass>>>();
		initializeFacetMap();
	}
	
	
	/**
	 * Initialize the facet mappings
	 */
	private void initializeFacetMap() {
		facetMap.put("Function", functFillers);
		facetMap.put("Technique", techFillers);
	}
	
	
	/**
	 * For every subclass of equipment, compute all fillers of the 3 facets: hasFunction, usesTechnique, and hasLocation
	 */
	public void classify() {
		for(String c : getAllSubclassNames("Equipment")) {
			Query q1 = getFillers(c, "hasFunction");
			functFillers.put(q1.getChosenConcept(), q1.getFillers());
			
			Query q2 = getFillers(c, "usesTechnique");
			techFillers.put(q2.getChosenConcept(), q2.getFillers());
		}
	}
	
	
	/**
	 * Get usage of a concept
	 * @param c	Concept c
	 * @return Axioms that mention this concept
	 */
	public Set<String> getUsageOfConcept(String c) {
		return getManchesterRendering(ont.getReferencingAxioms(owlifyConcept(c)));
	}
	
	
	/**
	 * Get usage of an individual
	 * @param c	Individual c
	 * @return Axioms that mention this individual
	 */
	public Set<String> getUsageOfIndividual(String c) {
		return getManchesterRendering(ont.getReferencingAxioms(owlifyIndividual(c)));
	}
	
	
	/**
	 * Check whether a concept name is a subclass of another
	 * @param lhs	Subsumee-concept name
	 * @param rhs	Subsumer-concept name
	 * @return true if there exists a subclass relation, false otherwise
	 */
	public boolean isSubClassOf(String lhs, String rhs) {
		return reasoner.isEntailed(df.getOWLSubClassOfAxiom(owlifyConcept(lhs), owlifyConcept(rhs)));
	}
	
	
	/**
	 * Given a concept name, get the set of all its superclass names (including indirect ones)
	 * @param concept	Concept name
	 * @return Set of concept names
	 */
	public Set<String> getAllSuperclassNames(String concept) {
		Set<OWLClass> superclasses = reasoner.getSuperClasses(owlifyConcept(concept), false).getFlattened();
		return getManchesterRendering(superclasses);
	}
	
	
	/**
	 * Given a concept name, get the set of all its subclasses (including indirect ones) 
	 * @param concept	OWL class
	 * @return Set of concept names
	 */
	public Set<String> getAllSubclassNames(OWLClass c) {
		return getManchesterRendering(getAllSubclasses(c));
	}
	
	
	/**
	 * Given a concept name, get the set of all its subclasses (including indirect)
	 * @param concept	Concept name
	 * @return Set of concept names
	 */
	public Set<String> getAllSubclassNames(String concept) {
		return getManchesterRendering(getAllSubclasses(concept));
	}
	
	
	/**
	 * Given a concept name, get the set of all its subclasses (including indirect)
	 * @param concept	Concept name
	 * @return Set of OWL subclasses
	 */
	public Set<OWLClass> getAllSubclasses(String concept) {
		return getAllSubclasses(owlifyConcept(concept));
	}
	
	
	/**
	 * Given a concept name, get the set of all its subclasses (including indirect)
	 * @param concept	OWL class
	 * @return Set of OWL subclasses
	 */
	public Set<OWLClass> getAllSubclasses(OWLClass concept) {
		return reasoner.getSubClasses(concept, false).getFlattened();
	}
	
	
	/**
	 * Get direct OWL subclasses of a given concept, and whether each of those has subclasses
	 * @param c	OWL class
	 * @return A map of direct OWL subclasses and whether each of these has subclasses
	 */
	public Map<OWLClass,Boolean> getDirectSubclasses(OWLClass c) {
		Map<OWLClass,Boolean> map = new HashMap<OWLClass,Boolean>();
		for(OWLClass sub : reasoner.getSubClasses(c, true).getFlattened()) {
			if(!sub.isBottomEntity() && !sub.isTopEntity() && !sub.equals(c)) {
				Set<OWLClass> subcs = reasoner.getSubClasses(sub, true).getFlattened();
				subcs.remove(bottom);
				map.put(sub, !subcs.isEmpty());
			}
		}
		return map;
	}
	
	
	/**
	 * Get direct subclass names of a given class, and whether each of those has subclasses
	 * @param concept	Concept name
	 * @return A map of direct subclass names and whether each of these has subclasses
	 */
	public Map<String,Boolean> getDirectSubclasses(String concept) {
		Map<String,Boolean> map = new HashMap<String,Boolean>();
		Map<OWLClass,Boolean> classMap = getDirectSubclasses(owlifyConcept(concept));
		for(OWLClass subc : classMap.keySet()) {
			map.put(getManchesterRendering(subc), classMap.get(subc));
		}
		return map;
	}
	
	
	/**
	 * Get the set of concept names that are a subclass of the specified restriction 
	 * @param role	Role name
	 * @param filler	Filler concept name
	 * @return Set of concept names that have the specified role & filler successor
	 */
	public Set<String> getSubclassesForFiller(String role, String filler) {
		Set<String> result = new HashSet<String>();
		OWLClass f = owlifyConcept(filler);
		HashMap<OWLClass,Set<OWLClass>> map = facetMap.get(role);
		for(OWLClass c : map.keySet()) {
			if(map.get(c).contains(f))
				result.add(getManchesterRendering(c));
		}
		return result;
	}
	
	
	/**
	 * Get the set of instance names that have the specified restriction 
	 * @param role	Role name
	 * @param filler	Filler concept name
	 * @return Set of concept names that have the specified role & filler successor
	 */
	public Set<String> getTypesOfInstance(String ind) {
		OWLNamedIndividual f = owlifyIndividual(ind);
		Set<OWLClass> types = reasoner.getTypes(f, false).getFlattened();
		return getManchesterRendering(types);
	}
	
	
	/**
	 * Get the set of instance names that have the specified restriction 
	 * @param role	Role name
	 * @param filler	Filler concept name
	 * @return Set of concept names that have the specified role & filler successor
	 */
	public Set<String> getTypesOfHasLocation(String ind) {
		OWLNamedIndividual f = owlifyIndividual(ind);
		Set<OWLNamedIndividual> inds = reasoner.getInstances(df.getOWLObjectHasValue(owlifyRole("hasLocation"), f), false).getFlattened();
		return getManchesterRendering(inds);
	}
	
	
	/**
	 * Check whether the given individual is an instance of the given concept
	 * @param ind	Individual name
	 * @param c	Concept name
	 * @return true if individual is an instance of the concept, false otherwise
	 */
	public boolean isInstanceOf(String ind, String c) {
		OWLIndividual individual = owlifyIndividual(ind);
		OWLClass concept = owlifyConcept(c);
		if(reasoner.isEntailed(df.getOWLClassAssertionAxiom(concept, individual)))
			return true;
		else
			return false;
	}
	
	
	/**
	 * Get all fillers F that hold for the query Concept => role some F 
	 * @param concept	Concept name
	 * @param role	Role name
	 * @return Query result
	 */
	public Query getFillers(String concept, String role) {
		Set<OWLClass> results = new HashSet<OWLClass>();
		OWLObjectProperty p = owlifyRole(role);
		OWLClass lhs = owlifyConcept(concept);
		
		Set<OWLClass> classes = null;
		if(role.equals("usesTechnique"))
			classes = getAllSubclasses("Technique");
		else if(role.equals("hasFunction"))
			classes = getAllSubclasses("Function");

		for(OWLClass c : classes) {
			OWLAxiom ax = df.getOWLSubClassOfAxiom(lhs, df.getOWLObjectSomeValuesFrom(p, c));
			if(reasoner.isEntailed(ax))
				results.add(c);
		}
		return new Query(lhs, p, results);
	}
	
	
	/**
	 * Get the set of fillers for the 'hasFunction' role on a given concept name
	 * @param c	Concept name
	 * @return A set of fillers for the 'hasFunction' role
	 */
	public Set<String> getFunctionFillers(String c) {
		return getFunctionFillers(owlifyConcept(c));
	}
	
	
	/**
	 * Get the set of fillers for the 'hasFunction' role on a given concept
	 * @param c	OWLClass
	 * @return A set of fillers for the 'hasFunction' role
	 */
	public Set<String> getFunctionFillers(OWLClass c) {
		Set<OWLClass> fillers = functFillers.get(c);
		if(fillers != null)
			return getClassNames(fillers);
		else
			return new HashSet<String>();
	}
	
	
	/**
	 * Get the set of fillers for the 'usesTechnique' role on a given concept name
	 * @param c	Concept name
	 * @return A set of fillers for the 'usesTechnique' role
	 */
	public Set<String> getTechniqueFillers(String c) {
		return getTechniqueFillers(owlifyConcept(c));
	}
	
	
	/**
	 * Get the set of fillers for the 'usesTechnique' role on a given concept
	 * @param c	OWLClass
	 * @return A set of fillers for the 'usesTechnique' role
	 */
	public Set<String> getTechniqueFillers(OWLClass c) {
		Set<OWLClass> fillers = techFillers.get(c);
		if(fillers != null)
			return getClassNames(fillers);
		else
			return new HashSet<String>();
	}
	
	
	/**
	 * Given a set of OWLClass objects, get the set of corresponding class names
	 * @param classes	Set of OWLClass objects
	 * @return Set of class names
	 */
	public Set<String> getClassNames(Set<OWLClass> classes) {
		Set<String> result = new HashSet<String>();
		for(OWLClass c : classes) {
			result.add(getManchesterRendering(c));
		}
		return result;
	}
	
	
	/**
	 * Get instances of a given concept name
	 * @param concept	Concept name
	 * @return Set of instance names
	 */
	public Set<String> getInstancesOf(String concept) {
		return getManchesterRendering(reasoner.getInstances(owlifyConcept(concept), false).getFlattened());		
	}
	
	
	/**
	 * Given a concept name, return an OWLClass object
	 * @param concept	Concept name
	 * @return OWLClass for the given concept
	 */
	public OWLClass owlifyConcept(String concept) {
		if(!concept.startsWith("http://"))
			concept = ontURI + concept;
		concept = concept.replaceAll(" ", "_");
		return df.getOWLClass(IRI.create(concept));
	}

	
	/**
	 * Given a role name, return an OWLObjectProperty object
	 * @param role	Role name
	 * @return OWLObjectProperty for the given role
	 */
	public OWLObjectProperty owlifyRole(String role) {
		if(!role.startsWith("http://"))
			role = ontURI + role;
		role = role.replaceAll(" ", "_");
		return df.getOWLObjectProperty(IRI.create(role));
	}
	
	
	/**
	 * Given an individual name, return an OWLIndividual object
	 * @param ind	Individual name
	 * @return OWLIndividual for the given concept
	 */
	public OWLNamedIndividual owlifyIndividual(String ind) {
		if(!ind.startsWith("http://"))
			ind = ontURI + ind;
		ind = ind.replaceAll(" ", "_");
		return df.getOWLNamedIndividual(IRI.create(ind));
	}
	
	
	/**
	 * Get Manchester syntax of a set of OWL classes
	 * @param objs	Set of OWL classes
	 * @return Set of Manchester syntax class names
	 */
	public Set<String> getManchesterRendering(Set<? extends OWLObject> objs) {
		Set<String> results = new HashSet<String>();
		for(OWLObject o : objs) {
			results.add(getManchesterRendering(o));
		}
		return results;
	}
	
	
	/**
	 * Get Manchester syntax of an OWL object
	 * @param obj	Instance of OWLObject
	 * @return Object name in Manchester syntax
	 */
	public String getManchesterRendering(OWLObject obj) {
		StringWriter wr = new StringWriter();
		ManchesterOWLSyntaxObjectRenderer render = new ManchesterOWLSyntaxObjectRenderer(wr, fp);
		render.setUseWrapping(false);
		obj.accept(render);
		String result = wr.getBuffer().toString();
		result = result.replaceAll("_", " ");
		return result;
	}
}
