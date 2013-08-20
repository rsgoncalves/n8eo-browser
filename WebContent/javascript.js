/*
Created by Rafael Goncalves
Information Management Group (IMG)
School of Computer Science
University of Manchester
Last updated: 25-Apr-2013
*/

// Toggle subtree & swap image
function toggleSubTree(id, img_id) {
    var e = document.getElementById(id);
    var img = document.getElementById(img_id);
    if (e.style.display == '') {
        e.style.display = 'none';
        img.src='images/button-closed.png';
    }
    else {
        e.style.display = '';
        img.src='images/button-open.png';
    }
}


// Swap plus or minus image accordingly
function swapImage(img_id) {
	var img = document.getElementById(img_id);
	if(img != null) {
		if(img.src == 'images/button-open.png')
			img.src = 'images/button-closed.png';
		else
			img.src = 'images/button-open.png';
	}
}


// Open subtree
function open(divid) {
	divid.style.display = '';
}


// Close subtree
function close(divid) {
	divid.style.display = 'none';
}


// Switch to open (minus) picture
function openPic(divid) {
	var img = document.getElementById(divid);
	if(img != null)
		img.src = 'images/button-open.png';
}


// Switch to closed (plus) picture
function closePic(divid) {
	var img = document.getElementById(divid);
	if(img != null)
		img.src = 'images/button-closed.png';
}


// Replace the details-pane text with the usage of a selected term
function showUsg(terms) {
	var ele = document.getElementById('details');
	var tArray = terms.split(';');
	var string = "<ul>";
	for(var i = 0; i < tArray.length; i++) {
		if(!tArray[i].startsWith('Class: ') && !tArray[i].startsWith('Individual: '))
			string += "<li style='list-style-type:square'>" + tArray[i] + "</li>";
	}
	string += "</ul>";
	ele.innerHTML = string; 
}


// Focus on the specified set of terms
function focusOn(term, tokens) {
	var eScrolled = false, tScrolled = false, fScrolled = false, iScrolled = false;
	var equip = false, tech = false, funct = false, instance = false;
	if(isTechniqueTerm(term)) {
		tech = true; tScrolled = true;
		console.log("Chosen technique: " + term);
	}
	else if(isFunctionTerm(term)) {
		funct = true; fScrolled = true;
		console.log("Chosen function: " + term);
	}
	else if(isEquipmentTerm(term)) {
		equip = true; eScrolled = true;
		console.log("Chosen equipment: " + term);
	}
	else if(isInstanceTerm(term)) {
		instance = true; iScrolled = true;
		console.log("Chosen instance: " + term);
	}
	
	unFocusAll();
	var tArray = tokens.split(',');
	if(tArray.length > 0) {
		console.log(" Total related terms: " + tArray.length);
		for(var i = 0; i < tArray.length; i++) {
			console.log("  Related term: " + tArray[i]);
			var t = false, f = false, e = false, ind = false;
			if(isTechniqueTerm(tArray[i]))		
				t = true;
			else if(isFunctionTerm(tArray[i]))
				f = true;
			else if(isEquipmentTerm(tArray[i])) 
				e = true;
			else if(isInstanceTerm(tArray[i]))
				ind = true;
			
			var els = document.getElementsByName(tArray[i]);
			for(var j = 0; j < els.length; j++) {	
				els[j].style.backgroundColor = '#fefdd8';
				els[j].style.color = '#101010';
			}
			
			if((equip && !e) || (tech && !t) || (funct && !f) || (instance && !i)) {
				if(f && !fScrolled) {
					console.log("   Scrolling Function-Pane to: " + tArray[i]);
					var success = scrollTo(tArray[i], 'function');
					if(success) fScrolled = true;
				}
				else if(t && !tScrolled) {
					console.log("   Scrolling Technique-Pane to: " + tArray[i]);
					var success = scrollTo(tArray[i], 'technique');
					if(success) tScrolled = true;
				}	
				else if(ind && !iScrolled) {
					console.log("   Scrolling Instance-Pane to: " + tArray[i]);
					var success = scrollTo(tArray[i], 'instances');
					if(success) iScrolled = true;
				}
				else if(e && !eScrolled) {
					console.log("   Scrolling Equipment-Pane to: " + tArray[i]);
					var success = scrollTo(tArray[i], 'equipment');
					if(success) eScrolled = true;
				}
			}
		}
	}
	highlightToken(term);
}


// Unfocus all terms
function unFocusAll() {
	var tArray = document.getElementsByTagName("a");
	for(var i = 0; i < tArray.length; i++) {
		if(tArray[i].classList.contains('concept') || tArray[i].classList.contains('instance')) {
			tArray[i].style.backgroundColor = '#feffff';
			tArray[i].style.color = '#8e8e8e';
		}
	}
}


// Focus on all terms
function focusAll() {
	var tArray = document.getElementsByTagName("a");
	for(var i = 0; i < tArray.length; i++) {
		if(tArray[i].classList.contains('concept') || tArray[i].classList.contains('instance')) {
			tArray[i].style.backgroundColor = '#feffff';
			tArray[i].style.color = '#101010';
		}
	}
}


// Highlight a specified term
function highlightToken(token) {
	unHighlightAllTokens();
	var t = document.getElementsByName(token);
	for(var i = 0; i < t.length; i++) {
		t[i].style.fontWeight = 'bold';
		t[i].style.backgroundColor = '#fefdd8';
		t[i].style.color = '#101010';
	}
}


// Unhighlight all terms
function unHighlightAllTokens() {
	var tArray = document.getElementsByTagName("a");
	for(var i = 0; i < tArray.length; i++) {
		if(tArray[i].style.fontWeight == 'bold') {
			tArray[i].style.fontWeight = 'normal';
			tArray[i].style.backgroundColor = '#feffff';
		}
	}
}


// Clear selected and striken out terms
function clearSelection() {
	focusAll();
	unHighlightAllTokens();
}


// Collapse all subtrees of the specified facet
function collapseAll(type) {
	var tArray = document.getElementsByTagName("ul");
	for(var i = 0; i < tArray.length; i++) {
		var ulid = tArray[i].id;
		if( (type == "function" && ulid.startsWith('To ') ) || (type == "technique" && ulid.indexOf(' Technique') != -1) 
				|| (type == "equipment" && !ulid.startsWith('To ') && !(ulid.indexOf(' Technique') != -1) )) {
			var imgId;
			if(tArray[i].id.indexOf('_Children') > -1) {
				close(tArray[i]);
				imgId = tArray[i].id.replace("_Children","_Img");
			}
			else {
				imgId = tArray[i].id.replace("_Main","_Img");
			}
			closePic(imgId);
		}
	}
}


// Expand all subtrees of the specified facet
function expandAll(type) {
	var tArray = document.getElementsByTagName("ul");
	for(var i = 0; i < tArray.length; i++) {
		var ulid = tArray[i].id;
		if( (type == "function" && ulid.startsWith('To ') ) || (type == "technique" && ulid.indexOf(' Technique') != -1) 
				|| (type == "equipment" && !ulid.startsWith('To ') && !(ulid.indexOf(' Technique') != -1) )) {
			var imgId;
			if(tArray[i].id.indexOf('_Children') > -1) {
				open(tArray[i]);
				imgId = tArray[i].id.replace("_Children", "_Img");
			}
			else
				imgId = tArray[i].id.replace("_Main", "_Img");
			
			openPic(imgId);
		}
	}
}


// Scroll the respective element container to a given element
function scrollTo(ele, container) {
	var success = false;
	console.log("     Triggered a scrollTo in '" + container + "' facet");
	var eles = document.getElementsByName(ele);
	if(eles != null && eles.length > 0) {
		if(container != null) {
			var pos = eles[0].offsetTop;
			console.log("       Element " + eles[0].name + " position: " + pos + " px");
			document.getElementById(container).scrollTop = pos;
			if(pos != 0) success = true;
		}
	}
	return success;
}


// Expand the trees (and swap images) of a given set of terms
function expand(terms) {	
	var tArray = terms.split(',');
	for(var i = 0; i < tArray.length; i++) {
		var ele = document.getElementById(tArray[i]);
		if(ele != null) {
			if(ele.style.display == 'none') {
				var eleId = ele.id;
				var imgId = eleId.replace('Children','Img');
				toggleSubTree(eleId, imgId);
			}
		}
	}
}


// Check whether the given term is a 'Equipment'-type term
function isEquipmentTerm(term) {
	var eles = document.getElementsByName(term);
	for(var i = 0; i < eles.length; i++) {
		if(eles[i].classList.contains('concept') && !isFunctionTerm(term) && !isTechniqueTerm(term))
			return true;
		else
			return false;
	}
}


// Check whether the given term is a 'Function'-type term
function isFunctionTerm(term) {
	var eles = document.getElementsByName(term);
	for(var i = 0; i < eles.length; i++) {
		if(eles[i].id.startsWith('To '))
			return true;
		else
			return false;
	}
}


// Check whether the given term is a 'Technique'-type term
function isTechniqueTerm(term) {
	var eles = document.getElementsByName(term);
	for(var i = 0; i < eles.length; i++) {
		if(eles[i].id.indexOf(' Technique') != -1)
			return true;
		else
			return false;
	}
}


// Check whether the given term is a 'Instance'-type term
function isInstanceTerm(term) {
	var eles = document.getElementsByName(term);
	for(var i = 0; i < eles.length; i++) {
		if(eles[i].classList.contains('instance') && !isTechniqueTerm(term) && !isFunctionTerm(term))
			return true;
		else
			return false;
	}
}


// String.startsWith() function
if(typeof String.prototype.startsWith != 'function') {
	String.prototype.startsWith = function (str){
		return this.slice(0, str.length) == str;
	};
}