//  ===================================================
//  =============== CONFIGURABLE PARAMS  ==============
//  ===================================================

var default_link_distance = 10;

// How far can we change default_link_distance?
// 0   - I don't care
// 0.5 - Change it as you want, but it's preferrable to have default_link_distance
// 1   - One does not change default_link_distance
var default_link_strength = 0.7;

var default_circle_radius = 15;

var show_names = true;
var show_inactive_elements = true;
var default_max_texts_length = 100;

var charge_multiplier = 200;

var dvgraph = objcdv.parse_dependencies_graph(dependencies);
var d3graph = dvgraph.d3jsGraph();

var w = window,
        d = document,
        e = d.documentElement,
        g = d.getElementsByTagName('body')[0],
        x = w.innerWidth || e.clientWidth || g.clientWidth,
        y = w.innerHeight || e.clientHeight || g.clientHeight;

//  ===================================================
//  =============== http://d3js.org/ Magic ===========
//  ===================================================

// https://github.com/mbostock/d3/wiki/Ordinal-Scales#categorical-colors
var color = d3.scale.category10();

var container = d3.select("#chart").append("svg")
        .attr("width", x)
        .attr("height", y)
        .style("overflow", "hidden");

//  ===================================================
//  =============== ZOOM LOGIC ========================
//  ===================================================

container.append("rect")
        .attr("width", x)
        .attr("height", y)
        .style("fill", "none")
        .style("pointer-events", "all")
        .call(d3.behavior.zoom().on("zoom", function () {
            svg.attr("transform", "translate(" + d3.event.translate + ")" + " scale(" + d3.event.scale + ")");
        }));


//  ===================================================
//  =============== FORCE LAYOUT ======================
//  ===================================================

var force = d3.layout.force()
        .charge(function (d) {
            return d.filtered ? 0 : -d.weight * charge_multiplier
        })
        .linkDistance(function (l) {
            return l.source.filtered || l.target.filtered ? 500 : radius(l.source) + radius(l.target) + default_link_distance
        })
        .size([x, y])
        .nodes(d3.values(d3graph.nodes))
        .links(d3graph.links)
        .linkStrength(function (l) {
            return l.source.filtered || l.target.filtered ? 0 : default_link_strength
        })
        .start();

var svg = container.append('g');
var actions = graph_actions.create(svg, dvgraph);

//  ===================================================
//  ===============  MARKERS SETUP   ==================
//  ===================================================

svg.append("defs").selectAll("marker")
        .data(["default", "dependency", "dependants"])
        .enter().append("marker")
        .attr("id", function (d) {
            return d;
        })
        .attr("viewBox", "0 -5 10 10")
        .attr("refX", 10)
        .attr("refY", 0)
        .attr("markerWidth", 10)
        .attr("markerHeight", 10)
        .attr("orient", "auto")
        .attr("class", "marker")
        .append("path")
        .attr("d", "M0,-5L10,0L0,5");


//  ===================================================
//  ===============  LINKS SETUP     ==================
//  ===================================================

var link = svg.append("g").selectAll("path")
        .data(d3graph.links)
        .enter().append("path")
        .attr("class", "link")
        .attr("marker-end", "url(#default)")
        .style("stroke-width", function (d) {
            return 1;
        });

//  ===================================================
//  ===============  NODES SETUP     ==================
//  ===================================================

var node = svg.append("g").selectAll("circle.node")
        .data(d3graph.nodes)
        .enter().append("circle")
        .attr("r", radius)
        .style("fill", function (d) {
            return color(d.group)
        })
        .attr("class", "node")
        .attr("source", function (d) {
            return d.source
        })
        .attr("dest", function (d) {
            return d.dest
        })
        .call(force.drag)
        .on("click", function (d) {
            if (d3.event.defaultPrevented) {
                return
            }
            actions.selectNodesStartingFromNode(d, 1);
            force.start();
        })
        .on("contextmenu", function (d) {
            if (d3.event.defaultPrevented) {
                return
            }
            // Don't actually show context menu
            d3.event.preventDefault();

            actions.selectNodesStartingFromNode(d);
            force.start();
        });

//  ===================================================
//  ===============  TEXT NODES SETUP     =============
//  ===================================================

var text = svg.append("g").selectAll("text")
        .data(force.nodes())
        .enter().append("text")
        .attr("visibility", "hidden")
        .text(function (d) {
            return d.name.substring(0, default_max_texts_length)
        });

//  ===================================================
//  ===============  LINK CLASS UPDATE    =============
//  ===================================================

function updateLinkVisibilities() {
    var paths = document.getElementsByClassName("link")
    for (i = 0; i < paths.length; i++) {
        var clazz = "link";
        var isActive = paths[i].className.baseVal.includes("dependants") || paths[i].className.baseVal.includes("dependency")
        if (isActive) {
            clazz += paths[i].className.baseVal.includes("dependants") ? " dependants" : "";
            clazz += paths[i].className.baseVal.includes("dependency") ? " dependency" : "";
        } else {
            clazz += !show_inactive_elements ? " hidden" : "";
        }
        paths[i].className.baseVal = clazz;
    }
}

function updateNodeVisibilities() {
    var nodes = document.getElementsByClassName("node")
    for (i = 0; i < nodes.length; i++) {
        var clazz = "node";
        var isFiltered = nodes[i].className.baseVal.includes("filtered")
        if (isFiltered) {
            clazz += " filtered";
            clazz += !show_inactive_elements ? " hidden" : "";
        }
        nodes[i].className.baseVal = clazz;
    }
}
//  ===================================================
//  ===============  FORCE UPDATE        =============
//  ===================================================

force.on("tick", function (e) {
    svg.selectAll(".node").attr("r", radius);
    link.attr("d", link_line);
    node.attr("transform", transform);
    if (show_names) {
        text.attr("transform", transform);
        text.attr("visibility", "visible");
    }
    updateLinkVisibilities();
    updateNodeVisibilities();
});

//  ===================================================
//  ===============  HELPER FUNCTIONS     =============
//  ===================================================
function link_line(d) {
    var dx = d.target.x - d.source.x,
            dy = d.target.y - d.source.y,
            dr = Math.sqrt(dx * dx + dy * dy);

    var rsource = radius(d.sourceNode) / dr;
    var rdest = radius(d.targetNode) / dr;
    var startX = d.source.x + dx * rsource;
    var startY = d.source.y + dy * rsource;

    var endX = d.target.x - dx * rdest;
    var endY = d.target.y - dy * rdest;
    return "M" + startX + "," + startY + "L" + endX + "," + endY;
}

function transform(d) {
    return "translate(" + d.x + "," + d.y + ")";
}

function radius(d) {
    return default_circle_radius + default_circle_radius * d.source / 10;
}

/*
    Window resize update
    */
w.onresize = function () {
    x = w.innerWidth || e.clientWidth || g.clientWidth;
    y = w.innerHeight || e.clientHeight || g.clientHeight;

    container.attr("width", Math.ceil(x)).attr("height", Math.ceil(y));
    force.size([Math.ceil(x), Math.ceil(y)]).start();
};

//  ===================================================
//  =============== INPUTS HANDLING      ==============
//  ===================================================
d3.selectAll("input").on("change", function change() {

    if (this.name == "circle_size") {
        default_circle_radius = parseInt(this.value);
        force.linkDistance(function (l) {
            return radius(l.source) + radius(l.target) + default_link_distance;
        });
    }

    if (this.name == "charge_multiplier") {
        charge_multiplier = parseInt(this.value);
    }

    if (this.name == "link_strength") {
        default_link_strength = parseInt(this.value) / 10;
        force.linkStrength(default_link_strength);
    }

    if (this.name == "show_names") {
        text.attr("visibility", this.checked ? "visible" : "hidden");
        show_names = this.checked;
    }

    if (this.name == "show_inactive_elements") {
        show_inactive_elements = this.checked;
    }
    force.start();
});
