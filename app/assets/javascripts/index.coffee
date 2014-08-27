$ ->
  ws = new WebSocket $("body").data("ws-url")
  ws.onmessage = (event) ->
    message = JSON.parse event.data
    switch message.type
      when "field"
        $("#field").html(message.msg)
      when "message"
        $("#board tbody").append("<tr><td>" + message.uid + "</td><td>" + message.msg + "</td></tr>")
      else
        console.log(message)

  $("#buttons button").click (event) ->
    event.preventDefault()
    console.log(JSON.stringify({move: event.target.id}))
    ws.send(JSON.stringify({move: event.target.id}))

  $("#msgform").submit (event) ->
    event.preventDefault()
    console.log($("#msgtext").val())
    # send the message to watch the stock
    ws.send(JSON.stringify({msg: $("#msgtext").val()}))
    # reset the form
    $("#msgtext").val("")
