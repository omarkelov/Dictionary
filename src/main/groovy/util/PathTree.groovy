package util

import groovy.transform.CompileStatic

import static Server.SITE_NAME

@CompileStatic
class PathTree implements Comparable<PathTree>, Iterable<PathTree> {
    int level
    String name
    PathTree parent
    Map<String, PathTree> children = new TreeMap<>()

    void insert(String path) {
        String[] pathParts = path.split('/', 2)

        children.putIfAbsent(pathParts[0], new PathTree(level: level + 1, name: pathParts[0], parent: this))

        if (pathParts.length > 1 && pathParts[1]) {
            children.get(pathParts[0]).insert(pathParts[1])
        }
    }

    void insertAll(Collection<String> paths) {
        paths.each {
            insert(it)
        }
    }

    String getPath() {
        ArrayList<String> names = new ArrayList<>()

        PathTree pathTree = this
        while (pathTree.name != SITE_NAME) {
            names.add(pathTree.name)

            pathTree = pathTree.parent
        }

        StringJoiner pathStringJoiner = new StringJoiner('/')
        names.reverse().each {
            pathStringJoiner.add(it)
        }

        pathStringJoiner.toString()
    }

    @Override
    int compareTo(PathTree pathTree) {
        name <=> pathTree.name
    }

    @Override
    Iterator<PathTree> iterator() {
        LinkedList<PathTree> traverseList = new LinkedList<>()
        traverseList.addLast(this)

        return new Iterator<PathTree>() {
            @Override
            boolean hasNext() {
                !traverseList.isEmpty()
            }

            @Override
            PathTree next() {
                checkForPresence()

                traverseList.addAll(1, traverseList.peekFirst().children.values())

                traverseList.removeFirst()
            }

            private void checkForPresence() {
                if (!hasNext()) {
                    throw new NoSuchElementException('NO_MORE_ELEMENTS_EXCEPTION_MESSAGE')
                }
            }
        }
    }
}
