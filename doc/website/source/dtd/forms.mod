<!-- ====================================================================== -->
<!-- Website DTD Forms Module V2.0b1
     Part of the Website distribution
     http://sourceforge.net/projects/docbook/

     Please direct all questions and comments about this DTD to
     Norman Walsh, <ndw@nwalsh.com>.
                                                                            -->
<!-- ====================================================================== -->

<!ENTITY % events
 "onclick		CDATA		#IMPLIED
  ondblclick		CDATA		#IMPLIED
  onmousedown		CDATA		#IMPLIED
  onmouseup		CDATA		#IMPLIED
  onmouseover		CDATA		#IMPLIED
  onmousemove		CDATA		#IMPLIED
  onmouseout		CDATA		#IMPLIED
  onkeypress		CDATA		#IMPLIED
  onkeydown		CDATA		#IMPLIED
  onkeyup		CDATA		#IMPLIED"
>

<!ELEMENT %html-form.element; ((%component.mix;)|%html-input.element;|%html-button.element;
			|%html-label.element;|%html-select.element;|%html-textarea.element;)+>

<!ATTLIST %html-form.element;
	%html-xmlns;	CDATA	#FIXED %html-namespace;
	%common.attrib;
	%events;
	action		CDATA		#REQUIRED
	method		(GET|POST)	"GET"
	onsubmit	CDATA		#IMPLIED
	onreset		CDATA		#IMPLIED
>

<!ENTITY % inputtype  "(text | password | checkbox | radio
                       | submit | reset | file | hidden | image | button)">

<!ELEMENT %html-input.element; EMPTY>
<!ATTLIST %html-input.element;
	%html-xmlns;	CDATA	#FIXED %html-namespace;
	%common.attrib;
	%events;
	type		%inputtype;	"text"
	name		CDATA		#IMPLIED
	value		CDATA		#IMPLIED
	checked		(checked)	#IMPLIED
	disabled	(disabled)	#IMPLIED
	readonly	(readonly)	#IMPLIED
	size		CDATA		#IMPLIED
	maxlength	CDATA		#IMPLIED
	src		CDATA		#IMPLIED
	alt		CDATA		#IMPLIED
	usemap		CDATA		#IMPLIED
	tabindex	CDATA		#IMPLIED
	accesskey	CDATA		#IMPLIED
	onfocus		CDATA		#IMPLIED
	onblur		CDATA		#IMPLIED
	onselect	CDATA		#IMPLIED
	onchange	CDATA		#IMPLIED
>

<!ELEMENT %html-button.element; (%para.char.mix;)*>
<!ATTLIST %html-button.element;
	%html-xmlns;	CDATA	#FIXED %html-namespace;
	%common.attrib;
	%events;
	name		CDATA		#IMPLIED
	value		CDATA		#IMPLIED
	type		(button|submit|reset)	"submit"
	disabled	(disabled)	#IMPLIED
	tabindex	CDATA		#IMPLIED
	accesskey	CDATA		#IMPLIED
	onfocus		CDATA		#IMPLIED
	onblur		CDATA		#IMPLIED
>

<!ELEMENT %html-label.element; (%para.char.mix;)*>
<!ATTLIST %html-label.element;
	%html-xmlns;	CDATA	#FIXED %html-namespace;
	%common.attrib;
	%events;
	for		IDREF		#IMPLIED
	accesskey	CDATA		#IMPLIED
	onfocus		CDATA		#IMPLIED
	onblur		CDATA		#IMPLIED
>

<!ELEMENT %html-select.element; (%html-option.element;)+>
<!ATTLIST %html-select.element;
	%html-xmlns;	CDATA	#FIXED %html-namespace;
	%common.attrib;
	%events;
	name		CDATA		#IMPLIED
	size		CDATA		#IMPLIED
	multiple	(multiple)	#IMPLIED
	disabled	(disabled)	#IMPLIED
	tabindex	CDATA		#IMPLIED
	onfocus		CDATA		#IMPLIED
	onblur		CDATA		#IMPLIED
	onchange	CDATA		#IMPLIED
>

<!ELEMENT %html-option.element; (#PCDATA)>
<!ATTLIST %html-option.element;
	%html-xmlns;	CDATA	#FIXED %html-namespace;
	%common.attrib;
	%events;
	selected    	(selected)	#IMPLIED
	disabled    	(disabled)	#IMPLIED
	value		CDATA		#IMPLIED
>

<!ELEMENT %html-textarea.element; (#PCDATA)>
<!ATTLIST %html-textarea.element;
	%html-xmlns;	CDATA	#FIXED %html-namespace;
	%common.attrib;
	%events;
	name		CDATA		#IMPLIED
	rows		CDATA		#REQUIRED
	cols		CDATA		#REQUIRED
	disabled	(disabled)	#IMPLIED
	readonly	(readonly)	#IMPLIED
	tabindex	CDATA		#IMPLIED
	accesskey	CDATA		#IMPLIED
	onfocus		CDATA		#IMPLIED
	onblur		CDATA		#IMPLIED
	onselect	CDATA		#IMPLIED
	onchange	CDATA		#IMPLIED
>

<!-- End of forms.mod V2.0b1 .............................................. -->
<!-- ...................................................................... -->
