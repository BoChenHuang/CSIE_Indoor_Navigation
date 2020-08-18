package com.example.csie_indoor_navigation;

class Edge
{
    // specify which waypoint the edge is connected to
    final Node target;

    // the distance of the edge
    final double weight;

    // constructor of Edge object
    Edge(Node targetNode, double weightedDistanceToTarget) {
        target = targetNode;
        weight = weightedDistanceToTarget;
    }
}
