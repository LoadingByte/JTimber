0.2.0
-----

### Additions
* CollectionWrapper, ListWrapper and ArrayWrapper now accept all objects as elements (and not just parent-aware objects).
* Replace the WeakPAReference with the @Weak annotation on fields. The runtime hook directly interprets the annotation.
* Added the ParentAware.getSingleParent() method for scenarios where a certain parent-aware object should only have one parent.
* Added the ParentUtils utility class related to the parents of parent-aware objects.

### Fixes
* Removed an unnecessary dependency on the SLF4J API from the JTimber API.
* Fixed a bug which caused the ParentAware.addParent() method throw an exception when executed on parent-aware objects whose parent type could not be retrieved.

0.1.0
-----

### Notes
* This is the first iteration of the framework, so there aren't any changes to mention here.
