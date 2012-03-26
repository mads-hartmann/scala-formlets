
- Instead of using Either see if it isn't possible to use Box, more lift
  like, you know.

- Multiple submit buttons?

- Adding attributes to input fields etc. See if we can make it typesafe somehow.

- General way to handle default values. It would be nice if default values for
  the different inputs could do different things, for example if you have a default
  value for a radio button it should no longer be a formlet Option[String] as there's
  no way to de-select a radio button as far as I know

- Handle file uploads etc. (binary data)