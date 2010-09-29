jQuery(document).ready(function() {
    jQuery(".c-e,.c-c,.c-d").live("click", function() {
        var $this = jQuery(this);
        var $td = $this.attr("id") ? $this : jQuery("#" + $this.attr("d"));
        var expand = $td.is(".c-c");

    	toggle($td, expand, false);
    });
});

function collapseAll() {
	var $items = jQuery('.c-e');
	for (var i = $items.length - 1; i >= 0; i--) {
		toggle($items.eq(i), false, false);
	}
}

function expandAll() {
	var $items = jQuery('.c-c');
	for (var i = 0; i < $items.length; i++) {
		toggle($items.eq(i), true, false);
	}
}

function collapseOne() {
	var maxExpandedLevel = -1;
	
	jQuery(".c-e").filter(function() {
		var index = $(this).index();
		if (index > maxExpandedLevel) {
			maxExpandedLevel = index;
		}
	});
	
	if (maxExpandedLevel == -1) {
		return;
	}
	
	jQuery(".c-e").filter(function() {
		return $(this).index() == maxExpandedLevel;
	}).each(function() {
		toggle(jQuery(this), false, false);
	});
}

function expandOne() {
	var minExpandedLevel = Number.MAX_VALUE;
	
	jQuery(".c-c").filter(function() {
		var index = $(this).index();
		if (index < minExpandedLevel) {
			minExpandedLevel = index;
		}
	});
	
	jQuery(".c-c").filter(function() {
		return $(this).index() == minExpandedLevel;
	}).each(function() {
		toggle(jQuery(this), true, false);
	});
}

function toggle($td, expand, force) {
    var $tr = $td.parent();
    var rowCount = parseInt($td.attr("n"));
    var $items = $tr.siblings().slice($tr.index(), $tr.index() + rowCount - 1);
    
    if (expand) {
        $td.removeClass("c-c");
        $td.addClass("c-e");
        $items.removeClass("c-" + $td.index());
    } else {
        $td.removeClass("c-e");
        $td.addClass("c-c");
        $items.addClass("c-" + $td.index());
    }

    var $td2 = $td;
    while (($td2 = $td2.next()).length > 0) {
        if ($td2.is(".c-et")) {
            break;
        }
        if (!expand) {
            if ($td2.is(":visible")) {
                $td2.hide();
            }
        } else {
            if (!$td2.is(":visible")) {
                $td2.show();
            }
            if (!force && $td2.is(".c-c")) {
                break;
            }
        }
    }

    /*
    $td2 = $tr.children().last();
    if ($td2.is(".c-et")) {
        if (!$td2.attr("c")) {
            $td2.attr("c", $td2.attr("colspan"));
        }

        $td2.attr("colspan", parseInt($td2.attr("c")) + $tr.children().not(":visible").length);
    }
    */
}