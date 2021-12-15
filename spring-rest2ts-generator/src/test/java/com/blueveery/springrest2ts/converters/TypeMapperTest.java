package com.blueveery.springrest2ts.converters;

import org.assertj.core.util.Sets;
import org.junit.Test;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static com.blueveery.springrest2ts.converters.TypeMapper.countTypeDistance;
import static com.blueveery.springrest2ts.converters.TypeMapper.findNearestHierarchyRoot;
import static org.assertj.core.api.Assertions.assertThat;

public class TypeMapperTest {

    @Test
    public void classToItselfHasZeroDistance() {
        assertThat(countTypeDistance(Set.class, Set.class)).isZero();
        assertThat(countTypeDistance(HashSet.class, HashSet.class)).isZero();
    }

    @Test
    public void notRelatedClassHaveMaxDistance() {
        assertThat(countTypeDistance(Map.class, Collection.class)).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void sortedSetToSetHasDistanceOne() {
        assertThat(countTypeDistance(Set.class, SortedSet.class)).isOne();
    }

    @Test
    public void abstractSetTreeSetToHasDistanceOne() {
        assertThat(countTypeDistance(AbstractSet.class, TreeSet.class)).isOne();
    }

    @Test
    public void notRelatedRootsAreNotReturned() {
        assertThat(findNearestHierarchyRoot(Sets.set(Collection.class), HashMap.class)).isEmpty();
    }

    @Test
    public void nearestRootIsReturned() {
        assertThat(findNearestHierarchyRoot(Sets.set(Collection.class, Set.class), SortedSet.class)).hasValue(Set.class);
        assertThat(findNearestHierarchyRoot(Sets.set(Collection.class, Set.class, SortedSet.class), SortedSet.class)).hasValue(SortedSet.class);
        assertThat(findNearestHierarchyRoot(Sets.set(Collection.class, Set.class, HashSet.class), HashSet.class)).hasValue(HashSet.class);
    }
}