# paper
WYSWYG editor for compose

## What is this?
The [bug](https://issuetracker.google.com/issues/199768107) prevents the implementation  of rich text editor in compose. So the only solution is to create our own
wrappers for AnnotatedStrings and then use those wrappers to pass the annotated string to text field.
As Compose's `BasicTextField` drops spans when it is edited by a user, we need to create something to restore those tags.

