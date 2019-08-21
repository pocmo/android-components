workflow "Auto-Approve MickeyMoz PRs" {
  on = "pull_request"
  resolves = ["approve"]
}

action "filter" {
  uses = "actions/bin/filter@master"
  args = "MickeyMoz"
}

action "approve" {
  uses = "hmarr/auto-approve-action@master"
  needs = "filter"
  secrets = ["GITHUB_TOKEN"]
}
