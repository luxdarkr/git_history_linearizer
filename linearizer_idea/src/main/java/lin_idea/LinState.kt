package lin_idea

data class LinState (
    var stripCommitMessageCheckBox: Boolean = true,
    var fixCaseCheckBox: Boolean = true,
    var fixBadStartsCheckBox: Boolean = true,
    public var startCommitTextField: String = "",
    var refIDTextField: String = "",
    var repoTextField: String = "",
    var badStartsTextField: String = ""
)