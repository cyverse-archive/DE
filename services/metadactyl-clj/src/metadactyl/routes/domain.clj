(ns metadactyl.routes.domain
  (:use [ring.swagger.schema :only [describe]]
        [schema.core :only [defschema optional-key Any]])
  (:import [java.util UUID]))

(defschema AppParameterListItem
  {:id                         (describe UUID "A UUID that is used to identify the List Item")
   (optional-key :name)        (describe String "The List Item's name")
   (optional-key :value)       (describe String "The List Item's value")
   (optional-key :description) (describe String "The List Item's description")
   (optional-key :display)     (describe String "The List Item's display label")
   (optional-key :isDefault)   (describe Boolean "Flags this Item as the List's default selection")})

(defschema AppParameterListGroup
  (merge AppParameterListItem
         {(optional-key :arguments)
          (describe [AppParameterListItem] "The TreeSelector Group's arguments")

          (optional-key :groups)
          (describe [Any]
            "The TreeSelector Group's groups. This will be a list of more groups like this one, but
             the documentation library does not currently support recursive model schema definitions")}))

(defschema AppParameterListItemOrTree
  (merge AppParameterListItem
         {(optional-key :isSingleSelect)
          (describe Boolean "The TreeSelector root's single-selection flag")

          (optional-key :selectionCascade)
          (describe String "The TreeSelector root's cascace option")

          (optional-key :arguments)
          (describe [AppParameterListItem] "The TreeSelector root's arguments")

          (optional-key :groups)
          (describe [AppParameterListGroup] "The TreeSelector root's groups")}))

(defschema AppParameterValidator
  {:type
   (describe String
     "The validation rule's type, which describes how a property value should be validated. For
      example, if the type is `IntAbove` then the property value entered by the user must be an
      integer above a specific value, which is specified in the parameter list. You can use the
      `rule-types` endpoint to get a list of validation rule types")

   :params
   (describe [Any]
     "The list of parameters to use when validating a Parameter value. For example, to ensure that a
      Parameter contains a value that is an integer greater than zero, you would use a validation
      rule of type `IntAbove` along with a parameter list of `[0]`")})

(defschema AppParameter
  {:id
   (describe UUID "A UUID that is used to identify the Parameter")

   (optional-key :name)
   (describe String
     "The Parameter's name. In most cases, this field indicates the command-line option used to
      identify the Parameter on the command line. In these cases, the Parameter is assumed to be
      positional and no command-line option is used if the name is blank. For Parameters that
      specify a limited set of selection values, however, this is not the case. Instead, the
      Parameter arguments specify both the command-line flag and the Parameter value to use for each
      option that is selected")

   (optional-key :defalutValue)
   (describe Any "The Parameter's defalut value")

   (optional-key :label)
   (describe String "The Parameter's prompt to display in the UI")

   (optional-key :description)
   (describe String "The Parameter's description")

   (optional-key :order)
   (describe Long
     "The relative command-line order for the Parameter. If this field is not specified then the
      arguments will appear on the command-line in the order in which they appear in the import JSON.
      If you're not specifying the order, please be sure that the argument order is unimportant for
      the tool being integrated")

   (optional-key :required)
   (describe Boolean "Whether or not a value is required for this Parameter")

   (optional-key :isVisible)
   (describe Boolean "The Parameter's intended visibility in the job submission UI")

   (optional-key :omit_if_blank)
   (describe Boolean
     "Whether the command-line option should be omitted if the Parameter value is blank. This is
      most useful for optional arguments that use command-line flags in conjunction with a value. In
      this case, it is an error to include the command-line flag without a corresponding value. This
      flag indicates that the command-line flag should be omitted if the value is blank. This can
      also be used for positional arguments, but this flag tends to be useful only for trailing
      positional arguments")

   (optional-key :type)
   (describe String
     "The Parameter's type name. Must contain the name of one of the Parameter types defined in the
      database. You can get the list of defined and undeprecated Parameter types using the
      `parameter-types` endpoint")

   (optional-key :file_info_type)
   (describe String "The Input/Output Parameter's info type")

   (optional-key :is_implicit)
   (describe Boolean
     "Whether the Output Parameter name is specified on the command line (but still be referenced in
      Pipelines), or implicitly determined by the app itself. If the output file name is implicit
      then the output file name either must always be the same or it must follow a naming convention
      that can easily be matched with a glob pattern")

   (optional-key :data_source)
   (describe String "The Output Parameter's source")

   (optional-key :retain)
   (describe Boolean
     "Whether or not the Input should be copied back to the job output directory in iRODS")

   (optional-key :format)
   (describe String "The Input/Output Parameter's file format")

   (optional-key :arguments)
   (describe [AppParameterListItemOrTree]
     "The List Parameter's arguments. Only used in cases where the user is given a fixed number of
      values to choose from. This can occur for Parameters such as `TextSelection` or
      `IntegerSelection` Parameters")

   (optional-key :validators)
   (describe [AppParameterValidator]
     "The Parameter's validation rules, which contains a list of rules that can be used to verify
      that Parameter values entered by a user are valid. Note that in cases where the user is given
      a list of possibilities to choose from, no validation rules are required because the selection
      list itself can be used to validate the Parameter value")})

(defschema AppGroup
  {:id
   (describe UUID "A UUID that is used to identify the Parameter Group")

   (optional-key :name)
   (describe String "The Parameter Group's name")

   (optional-key :description)
   (describe String "The Parameter Group's description")

   (optional-key :label)
   (describe String "The label used to identify the Parameter Group in the UI")

   (optional-key :isVisible)
   (describe Boolean "The Parameter Group's intended visibility in the job submission UI")

   (optional-key :parameters)
   (describe [AppParameter] "The list of Parameters in this Group")})

(defschema App
  {:id                            (describe UUID "A UUID that is used to identify the App")
   :name                          (describe String "The App's name")
   :description                   (describe String "The App's description")
   (optional-key :label)          (describe String "The App's display name")
   (optional-key :published_date) (describe Long "The App's Date of public submission")
   (optional-key :edited_date)    (describe Long "The App's Date of its last edit")
   (optional-key :tool)           (describe String "The tool used to execute the App")
   (optional-key :tool_id)        (describe UUID "A UUID that is used to identify the App's tool")
   (optional-key :references)     (describe [String] "The App's references")

   (optional-key :groups)
   (describe [AppGroup] "The list of Parameter Groups associated with the App")})

