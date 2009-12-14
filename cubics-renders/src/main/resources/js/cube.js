function collapseAll() {
	toggle($('.x'), false, true);
}

function expandAll() {
	toggle($('.x'), true, true);
}

function toggle($this, expand, force) {
	toggleHierarchy($this, expand, force);
	
	if (expand) {
		$this.removeClass("c-c");
		$this.addClass("c-e");
	} else {
		$this.removeClass("c-e");
		$this.addClass("c-c");
	}
}

function toggleHierarchy($td, expanding, force) {
	var thisClass = $td.attr("class").split(' ')[0];
	
	$("." + thisClass).each(function() {
		
		if ($td[0] == this) {
			return;
		}

		var $this = $(this);
		
		if (expanding) {
			$this.show();
		} else {
			$this.hide();
		}
		
		if (force) {
			toggle($this, expanding, force);
		} else if ((expanding && !$this.is(".c-c")) || !expanding) {
			toggleHierarchy($this, expanding);
		}
	});
}

$(document).ready(function() {
	$("td").click(function() {
		var $this = $(this);
		
		var needToShow = $this.is(".c-c");
		
		toggle($this, needToShow, false);
	});
});

function collapseOne() {
	//	Collapse all expanded leafs that don't have (expanded) children
	$(".c-e:not('.c-ne.c-t')").filter(function() {
		var $next = $(this).next();
	    return $next.is(".c-c") 
	        || $next.is(".c-t") 
	        || $next.is(".c-ne");
	}).each(function() {
		toggle($(this), false, false);
	});
}

function expandOne() {
	//	Expand all collapsed leafs which parents are expanded
	$(".c-c:not('.c-ne.c-t')").filter(function() {
        var classes = $(this).attr("class").split(' ');
        return classes[0] == 'x' 
        	|| ($('#i' + classes[1]).is(".c-e"));
	}).each(function() {
		toggle($(this), true, false);
	});
}
