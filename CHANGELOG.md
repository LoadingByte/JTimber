0.2.0
-----

### Additions
* CollectionWrapper, ListWrapper and ArrayWrapper now accept all objects as elements (and not just parent-aware objects).
* Replace the WeakPAReference with the @Weak annotation on fields. The runtime hook directly interprets the annotation.
* Added the ParentAware.getSingleParent() method for scenarios where a certain parent-aware object should only have one parent.
* Added the ParentUtils utility class related to the parents of parent-aware objects.
* @SubstituteWithWrapper fields now also wrap objects when they are set. Therefore, no explicit wrapper creation is necessary anymore.
* Exceptions thrown because of misconfigured @SubstituteWithWrapper fields now actually provide some information for possible fixes.

### Removals
* The @Weak annotation no longer nulls itself if the referenced parent-aware object has no more parents. The feature was useless as it caused a lot of sudden null values.

### Fixes
* Removed an unnecessary dependency on the SLF4J API from the JTimber API.
* Removed unnecessary dependencies on unused parts of the ASM library.
* Remove the need for internal stack frame computation. Bugs with node classes which are not instrumented for some unknown reason should no longer occur.
* Fixed a bug which caused the ParentAware.addParent() method to throw an exception when executed on parent-aware objects whose parent type could not be retrieved.
* Fixed generated afterUnmarshal() methods throwing an exception if a @SubstituteWithWrapper field is null.
* Fixed a bug which caused @Weak fields whose disposal is "pending" (they haven't yet been retrieved in order to be lazily disposed) to be handled as existing children. Note that this fix is no longer relevant since the disposal feature has been removed.
* Node.getChildren() now returns the node's field values in the order the fields are defined in the class.

0.1.0
-----

### Notes
* This is the first iteration of the framework, so there aren't any changes to mention here.
