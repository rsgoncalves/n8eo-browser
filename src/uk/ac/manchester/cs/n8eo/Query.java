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

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;

/**
 * @author Rafael S. Goncalves <br/>
 * Information Management Group (IMG) <br/>
 * School of Computer Science <br/>
 * University of Manchester <br/>
 */
public class Query {
	private OWLClass concept;
	private OWLObjectProperty role;
	private Set<OWLClass> fillers;
	
	/**
	 * Query constructor
	 * @param concept	Concept on left hand side
	 * @param role	Role successor
	 * @param fillers	Set of fillers for the given role
	 */
	public Query(OWLClass concept, OWLObjectProperty role, Set<OWLClass> fillers) {
		this.concept = concept;
		this.role = role;
		this.fillers = fillers;
	}
	
	
	/**
	 * Get the chosen LHS concept 
	 * @return Concept on LHS of query
	 */
	public OWLClass getChosenConcept() {
		return concept;
	}
	
	
	/**
	 * Get the role successor
	 * @return Role successor
	 */
	public OWLObjectProperty getRoleSuccessor() {
		return role;
	}
	
	
	/**
	 * Get the set of fillers for the given role restriction
	 * @return Set of fillers for the given role restriction
	 */
	public Set<OWLClass> getFillers() {
		return fillers;
	}
}
