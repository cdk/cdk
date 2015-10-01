/*
 *
 */


// buffer stores previous styles for restoring the color
// of elements
var color_restore = new Map();

function get_svg(svg_id) {
  return document.getElementById(svg_id).contentDocument;
}

function recolor_all(svg_id, elem_id, color) {
  var svg   = get_svg(svg_id);
  var root  = svg.getElementById(elem_id);

  var atoms = root.getElementsByClassName('atom');
  var bonds = root.getElementsByClassName('bond');

  for (var i = 0; i < atoms.length; i++)
    recolor(atoms[i], color);    
  for (var i = 0; i < bonds.length; i++) 
    recolor(bonds[i], color);    
}

function decolor_all(svg_id) {
  var svg   = get_svg(svg_id);
  var elems = svg.getElementsByTagName('*');
  for (var i = 0; i < elems.length; i++)
    decolor(elems[i]);    
}

function recolor(element, color) {
  if (!element || element.nodeType != 1)
    return;
  var children = element.getElementsByTagName('*');
  for (var i = 0; i < children.length; i++) {
    recolor(children[i], color);
  }
  switch (element.tagName) {
    case 'line':
      if (!color_restore.has(element))
        color_restore.set(element, element.getAttribute('stroke'));
      element.style.stroke = color;
    break;
    case 'path':
      if (!color_restore.has(element))
        color_restore.set(element, element.getAttribute('fill'));
      element.style.fill   = color;
    break;
  }
} 

function decolor(element) {
  if (!color_restore.has(element))
    return;
  var org_color = color_restore.get(element);
  switch (element.tagName) {
    case 'line':
      element.style.stroke = org_color;
    break;
    case 'path':
      element.style.fill   = org_color;
    break;
  }
}

// Hide an SVG element
function make_hidden(element) {
  if (!element || element.nodeType != 1)
    return;
  element.style.visibility = 'hidden';
}

// Make an SVG element visible
function make_visible(element) {
  if (!element || element.nodeType != 1)
    return; 
  element.style.visibility = 'visible';
}

function toggle_atom_map(svg_id, switch_id) {
  var svg   = get_svg(svg_id);
  var swtch = document.getElementById(switch_id);

  if (!svg || !swtch)
    return;

  var child = svg.getElementsByClassName('outerglow');
  for (var i = 0; i < child.length; ++i) {
    if(child[i].nodeType == 1) {
      if (swtch.checked)      
        make_visible(child[i]);
      else  
        make_hidden(child[i]);
    }
  }
}

function recolor_all_ids(svg_id, elem_ids) {
  decolor_all(svg_id);
  var svg = get_svg(svg_id);        
  for (var i = 0; i < elem_ids.length; i++) {
    var elem     = svg.getElementById(elem_ids[i]);
    var children = elem.getElementsByTagName('*');
    recolor(elem, '#ff0000');
    for (var j = 0; j < children.length; j++) {
      if (children[j].nodeType == 1) {
        recolor(children[j], '#ff0000');
      }
    }
  }  
}

function install_mouseover_callback(svg_id, elem_ids, func) {
  var svg = get_svg(svg_id);      

  var recolor_func = function() { recolor_all_ids(svg_id, elem_ids) };
  
  for (var i = 0; i < elem_ids.length; i++) {
    var elem     = svg.getElementById(elem_ids[i]);
    var children = elem.getElementsByTagName('*');
    elem.addEventListener("mouseenter", func, false);
    elem.addEventListener("mouseenter", recolor_func, false);
    for (var j = 0; j < children.length; j++) {
      if (children[j].nodeType == 1) {
        children[j].addEventListener("mouseenter", func, false);
        children[j].addEventListener("mouseenter", recolor_func, false);
      }
    }
  }

}