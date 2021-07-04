const vscode = require('vscode');
const { getHelpContent } = require('./helpContent')
module.exports = {
    helpFunc,
    linFunc,
    hello
}

function helpFunc() {
    const panel = vscode.window.createWebviewPanel(
        'help',
        'Help',
    );

    return panel.webview.html = getHelpContent();
};
function linFunc(rep, start, branch, strip, fix_case, remove, fix_messages) {
    let form = "Linearizer -l " + rep + " " + branch + " " + start
    const { exec } = require("child_process")
    if (rep == '' || start == '' || branch == '')
        vscode.window.showInformationMessage('Fix your lines')
    if (strip == true) {
        form = form + " " + "-s"
    }
    if (fix_case == true) {
        form = form + " " + "-f"
    }
    if (remove == true) {
        form = form + " " + "-r"
    }
    if (fix_messages == true) {
        form = form + " " + "-b"
    }
    exec(form, (error, stdout, stderr) => {
        if (error) {
            vscode.window.showInformationMessage(`error: ${error.message}`);
            return;
        }
        if (stderr) {
            vscode.window.showInformationMessage(`${stderr}`);
            return;
        }
        vscode.window.showInformationMessage(`Success! ${stdout}`);
    });
}

function hello() {
    vscode.window.showInformationMessage('Hello!');
}