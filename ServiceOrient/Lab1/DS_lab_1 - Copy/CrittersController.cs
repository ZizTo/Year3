using DS_lab_1.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace DS_lab_1
{

    [ApiController]
    [Route("api/[controller]")]
    public class CrittersController : ControllerBase
    {
        private readonly HockeyContext _context;
        public CrittersController(HockeyContext context) { _context = context; }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<Player>>> Get([FromQuery] string? position, [FromQuery] int? yearFrom, [FromQuery] int? yearTo)
        {
            var query = _context.Players.AsQueryable();
            if (!string.IsNullOrEmpty(position)) query = query.Where(p => p.Position == position);
            if (yearFrom.HasValue) query = query.Where(p => p.Birthday.Year >= yearFrom.Value);
            if (yearTo.HasValue) query = query.Where(p => p.Birthday.Year <= yearTo.Value);
            return await query.ToListAsync();
        }

        [HttpPut("{id}")]
        public async Task<IActionResult> Put(string id, [FromBody] PlayerUpdateDTO updated, [FromQuery] string? position, [FromQuery] int? yearFrom, [FromQuery] int? yearTo)
        {
            var original = await _context.Players.FindAsync(id);
            if (original == null) return NotFound();
            if (yearFrom.HasValue && original.Birthday.Year < yearFrom.Value) return BadRequest();
            if (yearTo.HasValue && original.Birthday.Year > yearTo.Value) return BadRequest();
            original.Birthday = original.Birthday;
            original.BirthState = updated.BirthState;
            await _context.SaveChangesAsync();
            return NoContent();
        }
    }
}
