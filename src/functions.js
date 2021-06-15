const vscode = require('vscode');
module.exports = {
    helpFunc,
    linFunc,
    hello,
    help,
    linearize
}

function helpFunc() {
    const { exec } = require("child_process");

exec("Linearizer -h", (error, stdout, stderr) => {
    if (error) {
        vscode.window.showInformationMessage(`error: ${error.message}`);
        return;
    }
    if (stderr) {
        vscode.window.showInformationMessage(`${stderr}`);
        return;
    }
    vscode.window.showInformationMessage(`${stdout}`);
});
};
function linFunc(rep, start, branch, strip, fix_case, remove, fix_messages) {
    if (rep == '' || start == '' || branch == '')
        vscode.window.showInformationMessage('Fix your lines')
    else {
        vscode.window.showInformationMessage(' Repository: ' + rep +
            ' Start: ' + start +
            ' Branch: ' + branch);
        if (strip == true)
            vscode.window.showInformationMessage('strip is on')
        if (fix_case == true)
            vscode.window.showInformationMessage('fix_case is on')
        if (remove == true)
            vscode.window.showInformationMessage('remove is on')
        if (fix_messages == true)
            vscode.window.showInformationMessage('fix_messages is on')
    }
};
function hello() {
    vscode.window.showInformationMessage('Hello!');
}

function help() {
    const { exec } = require("child_process");

    exec("Linearizer -h", (error, stdout, stderr) => {
        if (error) {
            vscode.window.showInformationMessage(`error: ${error.message}`);
            return;
        }
        if (stderr) {
            vscode.window.showInformationMessage(`${stderr}`);
            return;
        }
        vscode.window.showInformationMessage(`${stdout}`);
    });
}

function linearize(rep, start, branch) {
    const { exec } = require("child_process");
    exec("Linearizer -l" + rep + branch + start, (error, stdout, stderr) => {
        if (error) {
            vscode.window.showInformationMessage(`error: ${error.message}`);
            return;
        }
        if (stderr) {
            vscode.window.showInformationMessage(`${stderr}`);
            return;
        }
        vscode.window.showInformationMessage(`${stdout}`);
    });
}