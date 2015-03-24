(function ($) {
    $(document).ready(function () {
        var playerUpdateInterval, streamUpdateInterval;

        function createPlayerList(name) {
            var output = "<select name='" + name + "' id='" + name + "'>";
            output += "<option value=\"\" selected=\"selected\">--none--</option>"
            var players = $(".playerList .player");
            for (var i = 0; i < players.length; i++) {
                output += "<option value='" + $(players[i]).text() + "'>" + $(players[i]).text() + "</option>";
            }

            output += "</select>";
            return output;
        }

        function downloadStream() {
            $.ajax({
                dataType: "json",
                url: "/stream.html",
                success: function (data) {
                    if (data.status == "complete") {
                        var content = "";
                        showPlayerData(data.data);
                        for (var i = 0; i < data.data.length; i++) {
                            content += data.data[i] + "<br />";
                        }
                        $(".console .output").html(content);
                        var height = $('.console .output')[0].scrollHeight;
                        $('.console .output').scrollTop(height);
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    console.log([jqXHR, textStatus, errorThrown]);
                }
            });
        }

        function prepLayout() {
            // download all current Messages
            downloadStream();
            streamUpdateInterval = setInterval(downloadStream, 10000);

            $(".layout").show();
        }

        function showPlayerData(lines) {
            for (var i = 0; i < lines.length; i++) {
                var line = lines[i];
                var lineParts = line.split(" ");
                var test = lineParts[3] + " " + lineParts[4];
                if (test == "Connected players:") {
                    var output = "<h3>Player List</h3>"
                    var newLine = line.replace(lineParts[0] + " " + lineParts[1] + " " + lineParts[2] + " " + lineParts[3] + " " + lineParts[4], "");
                    if (newLine != "" && newLine.trim() != "") {
                        var users = newLine.split(", ");
                        for (var c = 0; c < users.length; c++) {
                            output += "<div class=\"player\">" + users[c].trim() + "</div>";
                        }
                    } else {
                        output += "<div class=\"noplayer\"> >>> No Players Online <<< </div>";
                    }
                }
            }
            $(".playerList").html(output);
        }

        function handlePardonPlayer() {
            downloadStream();
            clearInterval(streamUpdateInterval);
            $.ajax({
                dataType: "json",
                url: "/SendCommand.html?cmd=banlist",
                success: function (data) {
                    setTimeout(pardonPlayer, 500);
                }
            });
            streamUpdateInterval = setInterval(downloadStream, 5000);
        }

        function pardonPlayer() {
            $.ajax({
                dataType: "json",
                url: "/stream.html",
                success: function (data) {
                    var result = data.data.replace("<br />", "").split(" ");
                    remove = result[0] + " " + result[1] + " " + result[2] + " " + result[3] + " " + result[4] + " ";
                    var useData = data.data.replace("<br />", "").replace(remove, "");
                    useData = useData.split(", ");
                    var data = "<select id=\"pardonPlayerList\">";
                    if (useData[0] != "") {
                        for (var i = 0; i < useData.length; i++) {
                            data += "<option value=\"" + useData[i] + "\">" + useData[i] + "</option>";
                        }
                    }
                    data += "</select>";
                    var html = $("#pardonForm").html();
                    html = html.replace("{PardonList}", data);
                    $("#pardonForm").html(html);
                    $("#pardonDialog").dialog({
                        modal: true,
                        buttons: {
                            Ok: function () {
                                html = html.replace(data, "{PardonList}");
                                $("#pardonForm").html(html);
                                $(this).dialog("close");
                            }
                        }
                    });

                    $("#pardonForm").submit(function (e) {
                        e.preventDefault();
                        var player = $("#pardonPlayerList option:selected").val();
                        var cmd = "pardon%20" + player;
                        $.ajax({
                            dataType: "json",
                            url: "/SendCommand.html?cmd=" + cmd,
                            success: function (data) {
                                if (data.status == "complete") {
                                    clearInterval(streamUpdateInterval);
                                    setTimeout(downloadStream, 500);
                                    streamUpdateInterval = setInterval(downloadStream, 1000);
                                }
                            }
                        });
                        return false;
                    });
                }
            });
        }

        $("#loginForm").submit(function (e) {
            e.preventDefault();
            var username = $("#loginUsername").val();
            var password = $("#loginPassword").val();
            $.ajax({
                dataType: "json",
                url: "/ServerControl.html?action=login",
				type:"POST",
				data: "user=" + username + "&pass=" + password,
                success: function (data) {
                    if (data.status == "complete") {
                        $(".login").hide();
                        prepLayout();
                    } else {
                        $(".login .status").html("Login incorrect!");
                    }
                }
            });
            return false;
        });

        $("#consoleForm").submit(function (e) {
            e.preventDefault();
            sendConsoleCommand($("#consoleInput").val());
            $("#consoleInput").val("");
            return false;
        });

        $.ajax({
            dataType: "json",
            url: "/ServerControl.html?action=checkLogin",
            success: function (data) {
                if (data.status == true) {
                    $(".login").hide();
                    prepLayout();
                }
            }
        });

        function getWebAdminUsers() {
            $.ajax({
                dataType: "json",
                url: "/ServerControl.html?action=listUser",
                success: function (data) {
                    if (data.status == "complete") {
                        var html = "";
                        for (var i = 0; i < data.users.length; i++) {
                            html += data.users[i] + "<br />"
                        }

                        $("#dialog").html(html);
                        $("#dialog").dialog({
                            modal: true,
                            buttons: {
                                Ok: function () {
                                    $("#dialog").html("");
                                    $(this).dialog("close");
                                }
                            }
                        });
                    }
                }
            });
        }

        function createWebAdmin() {
            $("#addUserDialog").dialog({
                modal: true,
                buttons: {
                    Ok: function () {
                        $("#addUserDialog #addUsername").val("");
                        $("#addUserDialog #addPassword").val("");
                        $("#addUserForm .status").text("Please Enter Details");
                        $("#addUserForm .addForm").show();
                        $(this).dialog("close");
                    }
                }
            });

            $("#addUserForm").submit(function (e) {
                e.preventDefault();
                var username = $("#addUsername").val();
                var password = $("#addPassword").val();
                $.ajax({
                    dataType: "json",
                    url: "/ServerControl.html?action=createUser",
					type: "POST",
					data: "user=" + username + "&pass=" + password,
                    success: function (data) {
                        if (data.status == "complete") {
                            $("#addUserForm .status").text("User Created");
                            $("#addUserForm .addForm").hide();
                        } else {
                            $("#addUserForm .status").text("Failed does user exsits allready?");
                        }
                    }
                });
                return false;
            });
        }

        function restartWebAdmin() {
            $.ajax({
                dataType: "json",
                url: "/ServerControl.html?action=restartServer",
                success: function (data) {
                    if (data.status == "complete") {
                        $("#restartDialog").text("Server Restarting");
                    } else {
                        $("#restartDialog").text("Can't Restart Server");
                    }

                    $("#restartDialog").dialog({
                        modal: true,
                        buttons: {
                            Ok: function () {
                                $(this).dialog("close");
                            }
                        }
                    });
                }
            });
        }

        function removeWebAdmin() {
			$.ajax({
                dataType: "json",
                url: "/ServerControl.html?action=listUser",
                success: function (data) {
                    if (data.status == "complete") {
                        for (var i = 0; i < data.users.length; i++) {
							$("#delUsername").append("<option value=\""+data.users[i]+"\">"+data.users[i]+"</option>")
                        }
						$("#delUserDialog").dialog({
							modal: true,
							buttons: {
								Ok: function () {
									$("#delUsername option").remove();
									$("#delUserForm .status").text("Please Enter Details");
									$("#delUserForm .delForm").show();
									$(this).dialog("close");
								}
							}
						});

						$("#delUserForm").submit(function (e) {
							e.preventDefault();
							var username = $("#delUsername option:selected").val();
							$.ajax({
								dataType: "json",
								url: "/ServerControl.html?action=removeUser&user=" + username,
								success: function (data) {
									if (data.status == "complete") {
										$("#delUserForm .status").text("User Removed");
										$("#delUserForm .delForm").hide();
									} else {
										$("#delUserForm .status").text("Failed does user exsits?");
									}
								}
							});
							return false;
						});
                    }
                }
            });
        }

        function sendConsoleCommand(cmd) {
            clearInterval(streamUpdateInterval);
            $.ajax({
                dataType: "json",
                url: "/SendCommand.html?cmd=" + cmd,
                success: function (data) {
                    if (data.status == "complete") {
                        setTimeout(downloadStream, 500);
                    }
                }
            });
            streamUpdateInterval = setInterval(downloadStream, 5000);
        }

        function sendMessageCmd() {
            var html = $("#sendMsgForm").html();
            var playerList = createPlayerList("msgSendPlayer");
            html = html.replace("{PlayerList}", playerList)
            $("#sendMsgForm").html(html);

            $("#sendMsgDialog").dialog({
                modal: true,
                buttons: {
                    Ok: function () {
                        html = html.replace(playerList, "{PlayerList}")
                        $("#sendMsgForm").html(html);
                        $(this).dialog("close");
                    }
                }
            });

            $("#sendMsgForm").submit(function (e) {
                e.preventDefault();
                var playerName = $('#msgSendPlayer option:selected').val();
                var message = $("#msgSendMessage").val();
                var cmd = "tell%20" + playerName + "%20" + message
                $.ajax({
                    dataType: "json",
                    url: "/SendCommand.html?cmd=" + cmd,
                    success: function (data) {
                        if (data.status == "complete") {
                            clearInterval(streamUpdateInterval);
                            setTimeout(downloadStream, 500);
                            streamUpdateInterval = setInterval(downloadStream, 5000);
                        }
                    }
                });
                return false;
            });
        }

        function basicPlayerCommand(cmd) {
            var html = $("#basicCommandForm").html();
            $("#basicCommandForm").attr("title", "Perform '" + cmd + "' Command");
            var playerList = createPlayerList("basicCommandPlayer");
            html = html.replace("{PlayerList}", playerList).replace("Perform Command", "Perform " + cmd)
            $("#basicCommandForm").html(html);
            $("#basicCommandForm #basicCommandCmd").val(cmd)

            $("#basicCommandDialog").dialog({
                modal: true,
                buttons: {
                    Ok: function () {
                        $("#basicCommandForm").attr("title", "Perform Player Task");
                        html = html.replace(playerList, "{PlayerList}").replace("Perform " + cmd, "Perform Command")
                        $("#basicCommandForm").html(html);
                        $(this).dialog("close");
                    }
                }
            });

            $("#basicCommandForm").submit(function (e) {
                e.preventDefault();
                var playerName = $('#basicCommandPlayer option:selected').val();
                var input_cmd = $("#basicCommandCmd").val();
                var cmd = input_cmd + "%20" + playerName;
                $.ajax({
                    dataType: "json",
                    url: "/SendCommand.html?cmd=" + cmd,
                    success: function (data) {
                        if (data.status == "complete") {
                            clearInterval(streamUpdateInterval);
                            setTimeout(downloadStream, 500);
                            streamUpdateInterval = setInterval(downloadStream, 1000);
                        }
                    }
                });
                return false;
            });
        }

        function basicIPCommand(cmd) {
            var html = $("#ipDialogForm").html();
            $("#ipDialogForm").attr("title", "Perform IP " + cmd);
            html = html.replace("IP Command", "Perform " + cmd)
            $("#ipDialogForm").html(html);
            $("#ipDialogForm #ipDialogCmd").val(cmd)

            $("#ipDialog").dialog({
                modal: true,
                buttons: {
                    Ok: function () {
                        $("#ipDialogForm").attr("title", "Perform IP Task");
                        html = html.replace("Perform " + cmd, "IP Command")
                        $("#ipDialogForm").html(html);
                        $(this).dialog("close");
                    }
                }
            });

            $("#ipDialogForm").submit(function (e) {
                e.preventDefault();
                var ip = $('#ipDialogIP').val();
                var input_cmd = $("#ipDialogCmd").val();
                var cmd = input_cmd + "-ip%20" + ip;
                $.ajax({
                    dataType: "json",
                    url: "/SendCommand.html?cmd=" + cmd,
                    success: function (data) {
                        if (data.status == "complete") {
                            clearInterval(streamUpdateInterval);
                            setTimeout(downloadStream, 500);
                            streamUpdateInterval = setInterval(downloadStream, 1000);
                        }
                    }
                });
                return false;
            });
        }

        function broadcastMessage() {
            $("#broadcastDialog").dialog({
                modal: true,
                buttons: {
                    Ok: function () {
                        $("#broadcastMSG").val();
                        $(this).dialog("close");
                    }
                }
            });

            $("#broadcastForm").submit(function (e) {
                e.preventDefault();
                var message = $('#broadcastMSG').val();
                var cmd = "say%20" + message;
                $.ajax({
                    dataType: "json",
                    url: "/SendCommand.html?cmd=" + cmd,
                    success: function (data) {
                        if (data.status == "complete") {
                            clearInterval(streamUpdateInterval);
                            setTimeout(downloadStream, 500);
                            streamUpdateInterval = setInterval(downloadStream, 1000);
                        }
                    }
                });
                return false;
            });
        }

        function giveXPCommand() {
            var html = $("#xpForm").html();
            var playerList = createPlayerList("xpPlayer");
            html = html.replace("{PlayerList}", playerList);
            $("#xpForm").html(html);

            $("#xpDialog").dialog({
                modal: true,
                buttons: {
                    Ok: function () {
                        html = html.replace(playerList, "{PlayerList}");
                        $("#xpForm").html(html);
                        $(this).dialog("close");
                    }
                }
            });

            $("#xpForm").submit(function (e) {
                e.preventDefault();
                var playerName = $('#xpPlayer option:selected').val();
                var amount = $("#xpAmount").val();
                var cmd = "xp%20" + playerName + "%20" + amount;
                $.ajax({
                    dataType: "json",
                    url: "/SendCommand.html?cmd=" + cmd,
                    success: function (data) {
                        if (data.status == "complete") {
                            clearInterval(streamUpdateInterval);
                            setTimeout(downloadStream, 500);
                            streamUpdateInterval = setInterval(downloadStream, 1000);
                        }
                    }
                });
                return false;
            });
        }

        function gameModeAlter() {
            var html = $("#gameModeForm").html();
            var playerList = createPlayerList("gameModePlayer");
            html = html.replace("{PlayerList}", playerList);
            $("#gameModeForm").html(html);

            $("#gameModeDialog").dialog({
                modal: true,
                buttons: {
                    Ok: function () {
                        html = html.replace(playerList, "{PlayerList}");
                        $("#gameModeForm").html(html);
                        $(this).dialog("close");
                    }
                }
            });

            $("#gameModeForm").submit(function (e) {
                e.preventDefault();
                var playerName = $('#gameModePlayer option:selected').val();
                var mode = $('#gameModeMode option:selected').val();
                var cmd = "gamemode%20" + playerName + "%20" + mode;
                $.ajax({
                    dataType: "json",
                    url: "/SendCommand.html?cmd=" + cmd,
                    success: function (data) {
                        if (data.status == "complete") {
                            clearInterval(streamUpdateInterval);
                            setTimeout(downloadStream, 500);
                            streamUpdateInterval = setInterval(downloadStream, 1000);
                        }
                    }
                });
                return false;
            });
        }

        function giveItemCommand() {
            var html = $("#giveForm").html();
            var playerList = createPlayerList("giveItemPlayer");
            html = html.replace("{PlayerList}", playerList);
            $("#giveForm").html(html);

            $("#giveDialog").dialog({
                modal: true,
                buttons: {
                    Ok: function () {
                        html = html.replace(playerList, "{PlayerList}");
                        $("#giveForm").html(html);
                        $(this).dialog("close");
                    }
                }
            });

            $("#giveForm").submit(function (e) {
                e.preventDefault();
                var playerName = $('#giveItemPlayer option:selected').val();
                var item = $("#giveItem").val();
                var cmd = "give%20" + playerName + "%20" + item;
                $.ajax({
                    dataType: "json",
                    url: "/SendCommand.html?cmd=" + cmd,
                    success: function (data) {
                        if (data.status == "complete") {
                            clearInterval(streamUpdateInterval);
                            setTimeout(downloadStream, 500);
                            streamUpdateInterval = setInterval(downloadStream, 1000);
                        }
                    }
                });
                return false;
            });
        }

        function tpCommand() {
            var html = $("#teleportForm").html();
            var playerList = createPlayerList("teleportFrom");
            var whoList = createPlayerList("teleportTo");
            html = html.replace("{PlayerList}", playerList).replace("{WhoList}", whoList);
            $("#teleportForm").html(html);

            $("#teleportDialog").dialog({
                modal: true,
                buttons: {
                    Ok: function () {
                        html = html.replace(playerList, "{PlayerList}").replace(whoList, "{whoList}");
                        $("#teleportForm").html(html);
                        $(this).dialog("close");
                    }
                }
            });

            $("#teleportFrom").change(function () {
                var removed = $("#teleportRemoved").val();
                if (removed != undefined && removed != "") {
                    $("#teleportTo").append("<option value=\"" + removed + "\">" + removed + "</option>");
                }
                var playerName = $('#teleportFrom option:selected').val();

                if (playerName != "") {
                    $("#teleportRemoved").val(playerName);
                    $("#teleportTo option[value=" + playerName + "]").remove();
                }
            });

            $("#teleportForm").submit(function (e) {
                e.preventDefault();
                var playerName = $('#teleportFrom option:selected').val();
                var to = $('#teleportTo option:selected').val();
                var cmd = "tp%20" + playerName + "%20" + to;
                $.ajax({
                    dataType: "json",
                    url: "/SendCommand.html?cmd=" + cmd,
                    success: function (data) {
                        if (data.status == "complete") {
                            clearInterval(streamUpdateInterval);
                            setTimeout(downloadStream, 500);
                            streamUpdateInterval = setInterval(downloadStream, 1000);
                        }
                    }
                });
                return false;
            });
        }

        function installPlugin() {
            $("#pluginDialog").dialog({
                modal: true,
                buttons: {
                    Ok: function () {
                        $(this).dialog("close");
                    }
                }
            });

            $('#pluginDialog').ajaxForm({ url: 'ServerControl.html?action=installPlugin', type: 'post' });
        }

        function clearLogs() {
            $("#restartDialog").attr("title", "Clearing Logs");

            $.ajax({
                dataType: "json",
                url: "/ServerControl.html?action=restartLog",
                success: function (data) {
                    if (data.status == "complete") {
                        $("#restartDialog").text("Server Logs Cleared");
                    } else {
                        $("#restartDialog").text("Server Logs Clear Failed");
                    }
                    $("#pluginDialog").dialog({
                        modal: true,
                        buttons: {
                            Ok: function () {
                                $("#restartDialog").attr("title", "Server Restarting");
                                $("#restartDialog").text("");
                                $(this).dialog("close");
                            }
                        }
                    });
                }
            });
        }

        // player command
        $("#player_banlist").click(function () { sendConsoleCommand("banlist"); });
        $("#player_tell").click(function () { sendMessageCmd(); });
        $("#player_ban").click(function () { basicPlayerCommand("ban"); });
        $("#player_kick").click(function () { basicPlayerCommand("kick"); });
        $("#player_kill").click(function () { basicPlayerCommand("kill"); });
        $("#player_op").click(function () { basicPlayerCommand("op"); });
        $("#player_deop").click(function () { basicPlayerCommand("deop"); });
        $("#player_xp").click(function () { giveXPCommand(); });
        $("#player_gamemode").click(function () { gameModeAlter(); });
        $("#player_give").click(function () { giveItemCommand(); });
        $("#player_tp").click(function () { tpCommand(); });
        $("#player_pardon").click(function () { handlePardonPlayer(); });

        // Server commands
        $("#server_help").click(function () { sendConsoleCommand("help"); });
        $("#server_plugins").click(function () { sendConsoleCommand("plugins"); });
        $("#server_version").click(function () { sendConsoleCommand("version"); });
        $("#server_save").click(function () { sendConsoleCommand("save-all"); });
        $("#server_saveon").click(function () { sendConsoleCommand("save-on"); });
        $("#server_saveoff").click(function () { sendConsoleCommand("save-off"); });
        $("#server_reload").click(function () { sendConsoleCommand("reload"); });
        $("#server_rain").click(function () { sendConsoleCommand("toggledownfall"); });
        $("#server_ban").click(function () { basicIPCommand("ban"); });
        $("#server_pardon").click(function () { basicIPCommand("pardon"); });
        $("#server_say").click(function () { broadcastMessage(); });

        // Web Admin commands
        $("#web_list").click(function () { getWebAdminUsers(); });
        $("#web_add").click(function () { createWebAdmin(); });
        $("#web_del").click(function () { removeWebAdmin(); });
        $("#web_restart").click(function () { restartWebAdmin(); });
        $("#web_plugin").click(function () { installPlugin(); });
        $("#web_clear").click(function () { clearLogs(); });


        sendConsoleCommand("list");
    });
})(jQuery);