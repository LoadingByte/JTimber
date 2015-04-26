0.2.0
-----

### Additions
* CollectionWrapper, ListWrapper and ArrayWrapper now accept all objects as elements (and not just parent-aware objects).
* Replace the WeakPAReference with the @Weak annotation on fields. The runtime hook directly interprets the annotation.
* Added the ParentAware.getSingleParent() method for scenarios where a certain parent-aware object should only have one parent.
* Added the ParentUtils utility class related to the parents of parent-aware objects.
* @SubstituteWithWrapper fields now also wrap objects when they are set. Therefore, no explicit wrapper creation is necessary anymore.

### Fixes
* Removed an unnecessary dependency on the SLF4J API from the JTimber API.
* Fixed a bug which caused the ParentAware.addParent() method throw an exception when executed on parent-aware objects whose parent type could not be retrieved.
* Fixed generated afterUnmarshal() methods throwing an exception if a @SubstituteWithWrapper field is null.
* Fixed a bug which caused @Weak fields whose disposal is "pending" (they haven't yet been retrieved in order to be lazily disposed) to be handled as existing children. 

0.1.0
-----

### Notes
* This is the first iteration of the framework, so there aren't any changes to mention here.
